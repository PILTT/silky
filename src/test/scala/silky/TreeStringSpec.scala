package silky

import org.scalatest.{MustMatchers, Spec}

class TreeStringSpec extends Spec with MustMatchers {
  import TreeStringSpec._

  object `AnyTreeString can` {

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

    def `render a case class with 5-arity`: Unit = {
      Node5(
        suffix = Some("Zap"),
        top = Node1("Bap"),
        left = Node3("Bar", 3, List("cool stuff")),
        right = Node4("Baz", 5, Node3("Bar", 3, List("cool", "stuff"))),
        bottom = Node0).asTreeString mustBe
        """Node5(
          !- suffix = Some("Zap")
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
    }

    def `render a case class with a private parameter`: Unit =
      Node6("Bar").asTreeString mustBe "Node6()"
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
}
