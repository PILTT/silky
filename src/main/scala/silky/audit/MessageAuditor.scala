package silky.audit

import silky.MessageFlowId

trait MessageAuditor {

  def audit(messageFlowId: MessageFlowId, auditMessage: AuditMessage): Unit

  def auditToNamedLog(auditName: String, messageFlowId: MessageFlowId, auditMessage: AuditMessage): Unit
}
