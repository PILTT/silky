package silky.audit

import Headers._
import java.util.Date
import java.util.UUID._
import scala.collection.{Map, mutable}

object Headers {
  val MESSAGE_FLOW_ID = "Message-Flow-Id"
  val MESSAGE_ID = "Message-Id"
  val FROM = "From"
  val TO = "To"
}

case class AuditMessage(id: String = randomUUID.toString, from: String, to: String, payload: Any, timestamp: Date = new Date) {
  private val _headers: mutable.Map[String, String] = new mutable.LinkedHashMap[String, String]

  def withHeader(name: String, value: String): AuditMessage = {
    _headers += ((name, value))
    this
  }

  def headers: Map[String, String] = {
    val orderedHeaders = new mutable.LinkedHashMap[String, String]
    orderedHeaders += ((MESSAGE_ID, id))
    orderedHeaders += ((FROM, from))
    orderedHeaders += ((TO, to))
    orderedHeaders ++= _headers
  }
}
