package doradilla.proxy

import akka.actor.ActorRef
import doradilla.base.BaseActor
import doradilla.msg.TaskMsg.{EndRequest, RequestMsg, TaskResult, TaskStatus}
import doradilla.msg.TaskMsg.TaskStatus.TaskStatus
import doradilla.proxy.ProxyActor.{ProxyTaskResult, QueryProxy}

/**
  * For doradilla.proxy in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/30
  */
class ProxyActor(queueActor: ActorRef) extends BaseActor {
  var status: TaskStatus = TaskStatus.Unknown
  var replyTo: ActorRef = null
  var requestMsgBk: RequestMsg = null
  var fsmActor: ActorRef =null
  var result: Option[String] = None

  def doRequestMsg(requestMsg: RequestMsg): Unit = {
    replyTo = requestMsg.replyTo
    requestMsgBk =requestMsg
    queueActor ! RequestMsg(requestMsg.taskMsg, self, requestMsg.tranActor)
    status = TaskStatus.Queued
  }

  override def receive: Receive = {
    case requestMsg: RequestMsg => doRequestMsg(requestMsg)
    case taskResult:TaskResult => result = Some(taskResult.result)
      replyTo ! taskResult
      self ! TaskStatus.Finished
    case TaskStatus.Scheduled => fsmActor = sender()
      status =TaskStatus.Scheduled
    case TaskStatus.Finished | TaskStatus.Failed | TaskStatus.TimeOut =>
      fsmActor ! EndRequest(requestMsgBk)
    case msg: TaskStatus => status = msg
    case query: QueryProxy => sender() ! ProxyTaskResult(requestMsgBk, status, result)
  }
}

object ProxyActor {

  case class QueryProxy()

  case class ProxyTaskResult(requestMsg: RequestMsg, status: TaskStatus, result: Option[String])

}
