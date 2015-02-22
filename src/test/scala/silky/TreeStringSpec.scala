package silky

import org.scalatest.{MustMatchers, Spec}

import scalaz.NonEmptyList

class TreeStringSpec extends Spec with MustMatchers {
  import TreeStringSpec._

  object `The implicitly exposed TreeString.asTreeString can` {

    def `render a case object`: Unit =
      Node0.asTreeString mustBe "Node0"

    def `render a case class with 1-arity`: Unit =
      Node1("Bar").asTreeString mustBe "Node1(suffix = \"Bar\")"

    def `render a case class with 2-arity`: Unit =
      Node2("Bar", 3).asTreeString mustBe
        """Node2(
          !- suffix = "Bar"
          !- position = 3
          !)""".stripMargin('!')

    def `render a case class with 3-arity`: Unit =
      Node3("Bar", 3, List("stuff", "that", "works")).asTreeString mustBe
        """Node3(
          !- suffix = "Bar"
          !- position = 3
          !- names = List(
          !| - "stuff"
          !| - "that"
          !| - "works"
          !| )
          !)""".stripMargin('!')

    def `render a case class with 4-arity`: Unit =
      Node4("Baz", 5, Node3("Bar", 3, List("stuff", "that", "works"))).asTreeString mustBe
        """Node4(
          !- suffix = "Baz"
          !- position = 5
          !- contact = Node3(
          !| - suffix = "Bar"
          !| - position = 3
          !| - names = List(
          !| | - "stuff"
          !| | - "that"
          !| | - "works"
          !| | )
          !| )
          !)""".stripMargin('!')

    def `render a case class with 5-arity`: Unit =
      Node5(
        suffix = Some("Zap"),
        top    = Node1("Bap"),
        left   = Node3("Bar", 3, List("cool stuff")),
        right  = Node4("Baz", 5, Node3("Bar", 3, List("cool", "stuff"))),
        bottom = Node0).asTreeString mustBe
        """Node5(
          !- suffix = Some(x = "Zap")
          !- top = Node1(suffix = "Bap")
          !- left = Node3(
          !| - suffix = "Bar"
          !| - position = 3
          !| - names = List("cool stuff")
          !| )
          !- right = Node4(
          !| - suffix = "Baz"
          !| - position = 5
          !| - contact = Node3(
          !| | - suffix = "Bar"
          !| | - position = 3
          !| | - names = List(
          !| | | - "cool"
          !| | | - "stuff"
          !| | | )
          !| | )
          !| )
          !- bottom = Node0
          !)""".stripMargin('!')

    def `render a case class with a private parameter`: Unit =
      Node6("Bar").asTreeString mustBe "Node6()"

    def `render a class that does not override toString`: Unit =
      new Stuff(Node1("it"), Node1("well")).asTreeString mustBe
        """Stuff(
          !- top = Node1(suffix = "it")
          !- bottom = Node1(suffix = "well")
          !)""".stripMargin('!')

    def `render a class that overrides toString`: Unit =
      NonEmptyList(Node1("a"), Node1("b"), Node1("c")).asTreeString mustBe
        """NonEmptyList(
          !- Node1(suffix = "a")
          !- Node1(suffix = "b")
          !- Node1(suffix = "c")
          !)""".stripMargin('!')

    def `render a case class containing objects for which ShowTree instances exist`: Unit =
      Node7(new Stuff(Node1("it"), Node1("well")), NonEmptyList(Node1("a"), Node1("b"), Node1("c"))).asTreeString mustBe
        """Node7(
          !- contents = Stuff(
          !| - top = Node1(suffix = "it")
          !| - bottom = Node1(suffix = "well")
          !| )
          !- bits = NonEmptyList(
          !| - Node1(suffix = "a")
          !| - Node1(suffix = "b")
          !| - Node1(suffix = "c")
          !| )
          !)""".stripMargin('!')

    def `render a class containing objects for which ShowTree instances exist`: Unit =
      new Stuff2(Node7(new Stuff(Node1("it"), Node1("well")), NonEmptyList(Node1("a"), Node1("b"), Node1("c")))).asTreeString mustBe
        """Stuff2(node = Node7(
          !- contents = Stuff(
          !| - top = Node1(suffix = "it")
          !| - bottom = Node1(suffix = "well")
          !| )
          !- bits = NonEmptyList(
          !| - Node1(suffix = "a")
          !| - Node1(suffix = "b")
          !| - Node1(suffix = "c")
          !| )
          !))""".stripMargin('!')

    def `render a class with a missing optional parameter`: Unit =
      new Stuff3(None).asTreeString mustBe "Stuff3(node = None)"

    def `render a class containing optional objects for which ShowTree instances exist`: Unit =
      new Stuff3(
        Some(Node8(
          head = new OptionalStuff(Some(Node1("top")), None),
          tail = NonEmptyList(new OptionalStuff(Some(Node1("x")), Some(Node1("y"))))
        ))).asTreeString mustBe
        """Stuff3(node = Some(Node8(
          !- head = OptionalStuff(
          !| - top = Some(Node1(suffix = "top"))
          !| - bottom = None
          !| )
          !- tail = NonEmptyList(OptionalStuff(
          !| - top = Some(Node1(suffix = "x"))
          !| - bottom = Some(Node1(suffix = "y"))
          !| ))
          !)))""".
          stripMargin('!')
  }
}

object TreeStringSpec {
  case object Node0
  case class Node1(suffix: String)
  case class Node2(suffix: String, position: Int)
  case class Node3(suffix: String, position: Int, names: List[String])
  case class Node4(suffix: String, position: Int, contact: Node3)
  case class Node5(suffix: Option[String], top: Node1, left: Node3, right: Node4, bottom: Node0.type)
  case class Node6(private val suffix: String)
  case class Node7(contents: Stuff, bits: NonEmptyList[Node1])
  case class Node8(head: OptionalStuff, tail: NonEmptyList[OptionalStuff])

  class Stuff(val top: Node1, val bottom: Node1)
  class Stuff2(val node: Node7)

  class OptionalStuff(val top: Option[Node1], val bottom: Option[Node1])
  class Stuff3(val node: Option[Node8])

  implicit def showStuff: Option[ShowTree[Stuff]] = Some(new ShowTree[Stuff] {
    def treeStringOf(value: Stuff) = s"""Stuff(
      !${indent(Seq(s"top = ${value.top.asTreeString}", s"bottom = ${value.bottom.asTreeString}"))}
      !)""".stripMargin('!')
  })

  implicit def showStuff2(implicit sn7: Option[ShowTree[Node7]]): Option[ShowTree[Stuff2]] = Some(new ShowTree[Stuff2] {
    def treeStringOf(value: Stuff2) = s"Stuff2(node = ${sn7.get.treeStringOf(value.node)})"
  })

  implicit def showNode7(implicit ss: Option[ShowTree[Stuff]], sn: Option[ShowTree[NonEmptyList[Node1]]]): Option[ShowTree[Node7]] = Some(new ShowTree[Node7] {
    def treeStringOf(value: Node7) = s"""Node7(
      !${indent(Seq(s"contents = ${ss.get.treeStringOf(value.contents)}", s"bits = ${sn.get.treeStringOf(value.bits)}"))}
      !)""".stripMargin('!')
  })

  implicit def showOptionalStuff(implicit sn1: Option[ShowTree[Option[Node1]]]): Option[ShowTree[OptionalStuff]] = Some(new ShowTree[OptionalStuff] {
    def treeStringOf(value: OptionalStuff) = s"""OptionalStuff(
      !${indent(Seq(s"top = ${sn1.get.treeStringOf(value.top)}", s"bottom = ${sn1.get.treeStringOf(value.bottom)}"))}
      !)""".stripMargin('!')
  })

  implicit def showStuff3(implicit sn8: Option[ShowTree[Option[Node8]]]): Option[ShowTree[Stuff3]] = Some(new ShowTree[Stuff3] {
    def treeStringOf(value: Stuff3) = s"Stuff3(node = ${value.node.asTreeString})"
  })

  implicit def showNode8(implicit sos: Option[ShowTree[OptionalStuff]], sn: Option[ShowTree[NonEmptyList[OptionalStuff]]]): Option[ShowTree[Node8]] = Some(new ShowTree[Node8] {
    def treeStringOf(value: Node8) = s"""Node8(
      !${indent(Seq(s"head = ${sos.get.treeStringOf(value.head)}", s"tail = ${sn.get.treeStringOf(value.tail)}"))}
      !)""".stripMargin('!')
  })

  implicit def showNonEmptyList[T](implicit st: Option[ShowTree[List[T]]]): Option[ShowTree[NonEmptyList[T]]] = Some(new ShowTree[NonEmptyList[T]] {
    def treeStringOf(value: NonEmptyList[T]) = s"NonEmpty${st.get.treeStringOf(value.list)}"
  })
}
