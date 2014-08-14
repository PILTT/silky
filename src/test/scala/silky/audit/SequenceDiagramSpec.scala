package silky.audit

import java.util.{Date, TimeZone}

import clairvoyance.{CapturedValue, ProducesCapturedInputsAndOutputs}
import clairvoyance.plugins.SequenceDiagram
import clairvoyance.scalatest.ClairvoyantContext
import org.scalatest.{MustMatchers, Spec}
import silky.audit.Formatter._

import scala.io.Source
import scala.xml.parsing.ConstructingParser

class SequenceDiagramSpec extends Spec with MustMatchers with ClairvoyantContext with SequenceDiagram {

  override def capturedInputsAndOutputs: Seq[ProducesCapturedInputsAndOutputs] = Seq(this)

  private def parse(source: Source): Stream[AuditMessage] = {
    val parser = new Parser
    parser.dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))

    val stream = parser.parse(source)
    stream.foreach { message => captureValue(s"${classify(message.payload)} from ${message.from} to ${message.to}" -> message.payload) }
    stream
  }

  private def classify(payload: AnyRef): String = payload match {
    case "" => "Unknown"
    case s: String => ConstructingParser.fromSource(Source.fromString(s), preserveWS = false).document().docElem.label.capitalize
  }

  object `Silky is` {
    interestingGivens += ("initial value" -> "1234")

    def `can render a sequence diagram` {
      parse(fromString(
        """
          |1970-01-01 00:00:01,000 Begin: 123
          |Message-Id: 4babe38093
          |From: Bar
          |To: Foo
          |
          |End: 123
          |
          |1970-01-01 00:00:03,000 Begin: 456
          |Message-Id: 4babe38095
          |From: Foo
          |To: Bar
          |
          |<foo>value: 1234</foo>
          |
          |End: 456
          |
          |1970-01-01 00:00:05,000 Begin: 789
          |Message-Id: 4babe38097
          |From: Bar
          |To: Foo
          |
          |<bar>value: 5678, was: 1234</bar>
          |
          |End: 789
          |""")) mustBe Stream(
        AuditMessage(from = "Bar", to = "Foo", timestamp = new Date(1000L), id = "4babe38093", payload = ""),
        AuditMessage(from = "Foo", to = "Bar", timestamp = new Date(3000L), id = "4babe38095", payload = "<foo>value: 1234</foo>"),
        AuditMessage(from = "Bar", to = "Foo", timestamp = new Date(5000L), id = "4babe38097", payload = "<bar>value: 5678, was: 1234</bar>")
      )

    }
  }

  private def fromString(string: String): Source = Source.fromString(withoutMargin(string).trim)
}
