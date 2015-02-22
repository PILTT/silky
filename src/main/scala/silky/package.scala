import scala.annotation.implicitNotFound
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import scala.reflect.runtime._
import scala.util.Properties.lineSeparator

package object silky {

  @implicitNotFound("No member of type class ShowTree in scope for ${T}")
  trait ShowTree[T] {
    /** @return A tree-like string representation of this value */
    def treeStringOf(value: T): String
  }

  implicit def showOption[T](implicit st: Option[ShowTree[T]] = None): Option[ShowTree[Option[T]]] = Some(new ShowTree[Option[T]] {
    def treeStringOf(option: Option[T]) = st.fold(option.toString)(c ⇒ option.fold(None.toString)(v ⇒ s"Some(${c.treeStringOf(v)})"))
  })

  implicit def showProduct[T <: Product](implicit tag: ClassTag[T]): Option[ShowTree[T]] = Some(new ShowTree[T] {
    def treeStringOf(product: T) = productAsTreeString(product)
  })

  implicit def showList[T](implicit st: Option[ShowTree[T]] = None): Option[ShowTree[List[T]]] = Some(new ShowTree[List[T]] {
    def treeStringOf(list: List[T]) = traversableAsTreeString(list)(st)
  })

  implicit class TreeString[A](value: A) {

    /** @return A tree-like string representation of this value */
    def asTreeString(implicit converter: Option[ShowTree[A]] = None): String = value match {
      case (k, v)            ⇒ s"$k = ${v.asTreeString}"
      case v: Stream[_]      ⇒ treeStringOf(v)
      case v: Traversable[_] ⇒ traversableAsTreeString(v)
      case Left(v)           ⇒ s"Left(${v.asTreeString})"
      case Right(v)          ⇒ s"Right(${v.asTreeString})"
      case v: Product
        if converter.isEmpty ⇒ productAsTreeString(v)
      case v: String         ⇒ s""""$v""""
      case null              ⇒ "null"
      case v                 ⇒ converter.fold(v.toString)(_.treeStringOf(v))
    }

    private def treeStringOf(s: Stream[_]): String = {
      val result = s.map(_.asTreeString)
      if (result.size < 2) result.mkString(", ") else result.map(indent).mkString(lineSeparator)
    }
  }

  private def traversableAsTreeString[T](t: Traversable[T])(implicit st: Option[ShowTree[T]] = None): String =
    if (t.size < 2)
      s"${t.stringPrefix}(${t.map(_.asTreeString(st)).mkString(", ")})"
    else
      s"""${t.stringPrefix}($lineSeparator${
        t.map(_.asTreeString(st))
          .map(indent)
          .mkString(lineSeparator)
      }$lineSeparator)"""

  private def productAsTreeString[T <: Product](p: T)(implicit tag: ClassTag[T]) = {
    def isAccessible(ts: TermSymbol) = (ts.isVal || ts.isVar) && ts.getter != NoSymbol && !ts.getter.isPrivate

    val fields = currentMirror.reflect(p).symbol.typeSignature.members.toStream
      .collect { case a: TermSymbol ⇒ a }
      .filterNot(_.isMethod)
      .filterNot(_.isModule)
      .filterNot(_.isClass)
      .filter(s ⇒ s.isTerm && isAccessible(s.asTerm))
      .map(currentMirror.reflect(p).reflectField)
      .map(f ⇒ f.symbol.name.toString.trim → f.get)
      .reverse

    p.productArity match {
      case 0 ⇒ p.productPrefix
      case n if n < 2 ⇒ s"${p.productPrefix}(${fields.asTreeString})"
      case _ ⇒ s"${p.productPrefix}($lineSeparator${fields.asTreeString}$lineSeparator)"
    }
  }

  def indent(t: Seq[String]): String = t map indent mkString lineSeparator

  private def indent: String ⇒ String = string ⇒ string.lines.toStream match {
    case h +: t ⇒ (s"- $h" +: t.map{ "| " + _ }) mkString lineSeparator
    case _      ⇒ "- "
  }
}
