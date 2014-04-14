package silky.audit

import silky.MessageFlowId
import org.slf4j.LoggerFactory.getLogger

class Sl4jMessageAuditor(defaultLoggerName: String, messageFormatter: MessageFormatter) extends MessageAuditor {

  def audit(messageFlowId: MessageFlowId, auditMessage: AuditMessage) {
    auditToNamedLog(defaultLoggerName, messageFlowId, auditMessage)
  }

  def auditToNamedLog(auditLogName: String, messageFlowId: MessageFlowId, auditMessage: AuditMessage) {
    getLogger(auditLogName).info(messageFormatter.format(messageFlowId, auditMessage))
  }
}
