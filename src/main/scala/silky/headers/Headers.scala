package silky.headers

import silky.MessageFlowId

object Headers {
  val MESSAGE_FLOW_ID = "Message-Flow-Id"
  val MESSAGE_ID = "Message-Id"
  val FROM = "From"
  val TO = "To"

  def scopedSet(messageFlowIdValue: String, unitOfWork: () => Unit) {
    try {
      MessageFlowId.set(Some(MessageFlowId(messageFlowIdValue)))
      unitOfWork
    } finally {
      MessageFlowId.clear()
    }
  }
}
