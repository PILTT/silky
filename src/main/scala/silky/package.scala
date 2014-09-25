import scala.reflect.runtime.universe._
import scala.reflect.runtime._
import scala.util.Properties.lineSeparator

package object silky {

  implicit class TreeString[A](a: A) {

    /** @return A readable string representation of this value */
    def asTreeString: String = a match {
      case (k, v)            ⇒ s"$k = ${v.asTreeString}"
      case v: Stream[_]      ⇒ treeStringOf(v)
      case v: Traversable[_] ⇒ treeStringOf(v)
      case v: Product        ⇒ treeStringOf(v)
      case v: String         ⇒ s""""$v""""
      case null              ⇒ "null"
      case _                 ⇒ a.toString
    }

    private def treeStringOf(s: Stream[_]): String =
      s.map(_.asTreeString)
        .map(indent)
        .mkString(lineSeparator)

    private def treeStringOf(t: Traversable[_]): String =
      s"""${t.stringPrefix}($lineSeparator${
        t.map(_.asTreeString)
          .map(indent)
          .mkString(lineSeparator)
      }$lineSeparator)"""

    private def treeStringOf(p: Product): String = {
      val fields = currentMirror.reflect(p).symbol.typeSignature.members.toStream
        .collect { case a: TermSymbol ⇒ a}
        .filterNot(_.isMethod)
        .filterNot(_.isModule)
        .filterNot(_.isClass)
        .map(currentMirror.reflect(p).reflectField)
        .map(f ⇒ f.symbol.name.toString.trim → f.get)
        .reverse

      p.productArity match {
        case 0 ⇒ p.productPrefix
        case 1 ⇒ s"${p.productPrefix}(${fields.head._1} = ${fields.head._2.asTreeString})"
        case _ ⇒ s"${p.productPrefix}($lineSeparator${fields.asTreeString}$lineSeparator)"
      }
    }

    private def indent: String ⇒ String = string ⇒ string.lines.toStream match {
      case h +: t ⇒ (s"- $h" +: t.map{ "| " + _ }) mkString lineSeparator
      case _      ⇒ "- "
    }
  }
}
