package silky

import java.util.UUID.randomUUID
import scala.util.DynamicVariable

object MessageFlowId {
  private val messageFlowIds = new DynamicVariable[Option[MessageFlowId]](init = None)

  def set(messageFlowId: Option[MessageFlowId]): Option[MessageFlowId] = {
    messageFlowIds value_= messageFlowId
    messageFlowId
  }

  def get: Option[MessageFlowId] = messageFlowIds.value

  def makeOneUp: Option[MessageFlowId] = set(Some(MessageFlowId(randomUUID.toString)))

  def clear() {
    set(messageFlowId = None)
  }
}

case class MessageFlowId(value: String) {
  override def toString: String = value
}
