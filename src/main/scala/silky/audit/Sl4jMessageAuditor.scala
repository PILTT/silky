package silky.audit

import org.slf4j.LoggerFactory.getLogger
import silky.MessageFlowId

class Sl4jMessageAuditor(defaultLoggerName: String, messageFormatter: MessageFormatter) extends MessageAuditor {

  def audit(messageFlowId: MessageFlowId, auditMessage: AuditMessage) {
    auditToNamedLog(defaultLoggerName, messageFlowId, auditMessage)
  }

  def auditToNamedLog(auditLogName: String, messageFlowId: MessageFlowId, auditMessage: AuditMessage) {
    getLogger(auditLogName).info(messageFormatter.format(messageFlowId, auditMessage))
  }
}
