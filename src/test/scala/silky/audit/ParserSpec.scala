package silky.audit

import java.util.{Date, TimeZone}

import org.scalatest.{MustMatchers, Spec}
import silky.audit.Formatter._

import scala.io.Source

class ParserSpec extends Spec with MustMatchers {
  private val underTest = new Parser
  underTest.dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))

  object `AuditMessageParser must` {
    def `be able to parse an audit message with an empty payload` {
      underTest.parse(fromString(
        """
          |1970-01-01 00:00:00,000 Begin: 456
          |Message-Id: 4babe38095
          |From: Foo
          |To: Bar
          |
          |
          |End: 456
          |""")) mustBe Stream(
        AuditMessage(from = "Foo", to = "Bar", timestamp = new Date(0L), id = "4babe38095", payload = "")
      )
    }

    def `be able to parse an audit message with a non-empty payload` {
      underTest.parse(fromString(
        """
          |1970-01-01 00:00:00,000 Begin: 456
          |Message-Id: 4babe38095
          |From: Foo
          |To: Bar
          |
          |<foo>1</foo>
          |
          |End: 456
          |""")) mustBe Stream(
        AuditMessage(from = "Foo", to = "Bar", timestamp = new Date(0L), id = "4babe38095", payload = "<foo>1</foo>")
      )
    }

    def `be able to parse an audit message with custom headers` {
      underTest.parse(fromString(
        """
          |1970-01-01 00:00:00,000 Begin: 456
          |Message-Id: 4babe38095
          |From: Foo
          |To: Bar
          |Conversation: d16dfac6-0896-4e38-aae2-13147fdee4be
          |Duration: 33 milliseconds
          |
          |Hello World!
          |
          |End: 456
          |""")) mustBe Stream(
        AuditMessage(from = "Foo", to = "Bar", timestamp = new Date(0L), id = "4babe38095", payload = "Hello World!")
          .withHeader("Conversation", "d16dfac6-0896-4e38-aae2-13147fdee4be")
          .withHeader("Duration", "33 milliseconds")
      )
    }

    def `be able to parse multiple audit messages` {
      underTest.parse(fromString(
        """
          |some garbage
          |
          |1970-01-01 00:00:01,000 Begin: 123
          |Message-Id: 4babe38093
          |From: Bar
          |To: Foo
          |
          |End: 123
          |
          |more garbage
          |
          |1970-01-01 00:00:03,000 Begin: 456
          |Message-Id: 4babe38095
          |From: Foo
          |To: Bar
          |
          |<foo>1</foo>
          |
          |End: 456
          |
          |again more garbage
          |
          |1970-01-01 00:00:05,000 Begin: 789
          |Message-Id: 4babe38097
          |From: Bar
          |To: Foo
          |
          |<bar>2</bar>
          |
          |End: 789
          | """)) mustBe Stream(
        AuditMessage(from = "Bar", to = "Foo", timestamp = new Date(1000L), id = "4babe38093", payload = ""),
        AuditMessage(from = "Foo", to = "Bar", timestamp = new Date(3000L), id = "4babe38095", payload = "<foo>1</foo>"),
        AuditMessage(from = "Bar", to = "Foo", timestamp = new Date(5000L), id = "4babe38097", payload = "<bar>2</bar>")
      )
    }

    def `skip a message without identifier` {
      underTest.parse(fromString(
        """
          |1970-01-01 00:00:00,000 Begin: 456
          |From: Foo
          |To: Bar
          |
          |<foo>1</foo>
          |
          |End: 456
          |
          |1970-01-01 00:00:05,000 Begin: 789
          |Message-Id: 4babe38097
          |From: Bar
          |To: Foo
          |
          |<bar>2</bar>
          |
          |End: 789
          |""")) mustBe Stream(
        AuditMessage(from = "Bar", to = "Foo", timestamp = new Date(5000L), id = "4babe38097", payload = "<bar>2</bar>")
      )
    }

    def `skip a message without from` {
      underTest.parse(fromString(
        """
          |1970-01-01 00:00:00,000 Begin: 456
          |Message-Id: 4babe38095
          |To: Bar
          |
          |<foo>1</foo>
          |
          |End: 456
          |1970-01-01 00:00:05,000 Begin: 789
          |Message-Id: 4babe38097
          |From: Bar
          |To: Foo
          |
          |<bar>2</bar>
          |
          |End: 789
          |""")) mustBe Stream(
        AuditMessage(from = "Bar", to = "Foo", timestamp = new Date(5000L), id = "4babe38097", payload = "<bar>2</bar>")
      )
    }

    def `skip a message without to` {
      underTest.parse(fromString(
        """
          |1970-01-01 00:00:00,000 Begin: 456
          |Message-Id: 4babe38095
          |From: Foo
          |
          |<foo>1</foo>
          |
          |End: 456
          |
          |1970-01-01 00:00:05,000 Begin: 789
          |Message-Id: 4babe38097
          |From: Bar
          |To: Foo
          |
          |<bar>2</bar>
          |
          |End: 789
          |""")) mustBe Stream(
        AuditMessage(from = "Bar", to = "Foo", timestamp = new Date(5000L), id = "4babe38097", payload = "<bar>2</bar>")
      )
    }

    def `skip a message without a closing marker` {
      underTest.parse(fromString(
        """
          |1970-01-01 00:00:00,000 Begin: 456
          |Message-Id: 4babe38095
          |From: Foo
          |
          |<foo>1</foo>
          |
          |1970-01-01 00:00:05,000 Begin: 789
          |Message-Id: 4babe38097
          |From: Bar
          |To: Foo
          |
          |<bar>2</bar>
          |
          |End: 789
          |""")) mustBe Stream(
        AuditMessage(from = "Bar", to = "Foo", timestamp = new Date(5000L), id = "4babe38097", payload = "<bar>2</bar>")
      )
    }
  }

  private def fromString(string: String): Source = Source.fromString(withoutMargin(string).trim)
}
