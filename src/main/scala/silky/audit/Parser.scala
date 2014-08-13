package silky.audit

import java.text.{DateFormat, SimpleDateFormat}

import silky.audit.Headers._
import silky.audit.Parser._

import scala.io.Source
import scala.util.{Success, Try}

class Parser(val dateFormat: DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS")) {

  def parse(source: Source): Stream[AuditMessage] = parse(source.getLines())

  private def parse(lines: Iterator[String]): Stream[AuditMessage] = {
    if (lines.hasNext) {
      Try(parseMessage(lines)) match {
        case Success(Some(auditMessage)) => Stream.cons(auditMessage, parse(lines))
        case _ => parse(lines)
      }
    } else Stream.empty
  }

  private def parseMessage(lines: Iterator[String]): Option[AuditMessage] = {
    val line = lines.next()
    if (line.contains(BEGIN_MARKER)) {
      val timestamp = dateFormat.parse(line.substring(0, line.indexOf(BEGIN_MARKER)).trim)
      val messageFlowId = line.substring(line.indexOf(BEGIN_MARKER) + BEGIN_MARKER.length).trim
      val messageId = lines.next().substring(MESSAGE_ID.length + 1).trim
      val from = lines.next().substring(FROM.length + 1).trim
      val to = lines.next().substring(TO.length + 1).trim
      val headers = readHeaders(lines)

      val message = AuditMessage(messageId, from, to, readPayloadFor(lines), timestamp).withHeader(MESSAGE_FLOW_ID, messageFlowId)
      headers.foreach { case (name, value) => message.withHeader(name, value)}
      Some(message)
    } else None
  }

  private def readHeaders(lines: Iterator[String]): Map[String, String] =
    lines.takeWhile(_.contains(":")).map { line =>
      val entry = line.split(":")
      entry(0).trim -> entry(1).trim
    }.toMap

  private def readPayloadFor(lines: Iterator[String]): String = lines.takeWhile(!_.contains(END_MARKER)).mkString
}

object Parser {
  private val BEGIN_MARKER = "Begin: "
  private val END_MARKER   = "End: "
}
