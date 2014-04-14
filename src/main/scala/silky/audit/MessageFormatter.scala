package silky.audit

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.TimeZone
import scala.collection.Map
import silky.MessageFlowId

class MessageFormatter(dateFormat: DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS"),
                       timeZone: TimeZone = TimeZone.getTimeZone("UTC")) {

  dateFormat.setTimeZone(timeZone)

  def format(messageFlowId: MessageFlowId, auditMessage: AuditMessage): String =
    s"""${dateFormat.format(auditMessage.timestamp)} Begin: $messageFlowId
${asLines(auditMessage.headers)}

${auditMessage.payload}

End: $messageFlowId
""".toString

  private def asLines(headers: Map[String, String]) = headers
    .map(header => s"${header._1}: ${header._2}")
    .mkString("\n")
}
