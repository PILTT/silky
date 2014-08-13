package silky.audit

import java.util.{Date, TimeZone}
import org.scalatest._
import silky.MessageFlowId
import silky.audit.Formatter.withoutMargin

class FormatterSpec extends Spec with MustMatchers {
  private val messageFlowId = MessageFlowId("456")
  private val underTest = new Formatter
  underTest.dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))

  object `MessageFormatter must` {
    def `be able to format an audit message, given a message flow identifier` {
      val message = AuditMessage(from = "Foo", to = "Bar", timestamp = new Date(0L), id = "4babe38095", payload =
          <foo><bar>1</bar></foo>)

      underTest.format(messageFlowId, message) mustBe withoutMargin("""
      |1970-01-01 00:00:00,000 Begin: 456
      |Message-Id: 4babe38095
      |From: Foo
      |To: Bar
      |
      |<foo><bar>1</bar></foo>
      |
      |End: 456
      |""")
    }

    def `be able to format an audit message with custom headers` {
      val message = AuditMessage(from = "Foo", to = "Bar", timestamp = new Date(0L), id = "4babe38095", payload = "Hello World!")
        .withHeader("Conversation", "d16dfac6-0896-4e38-aae2-13147fdee4be")
        .withHeader("Duration", "33 milliseconds")

      underTest.format(messageFlowId, message) mustBe withoutMargin("""
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
      |""")
    }
  }
}
