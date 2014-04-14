package silky.audit

import scala.collection.{Map, mutable}
import java.util.Date
import silky.headers.Headers._
import java.util.UUID._

case class AuditMessage(timestamp: Date = new Date,
                        private val _headers: mutable.Map[String, String] = new mutable.LinkedHashMap[String, String],
                        private val id: String = randomUUID.toString,
                        private val from: String,
                        private val to: String,
                        payload: Any) {

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
    orderedHeaders
  }
}
