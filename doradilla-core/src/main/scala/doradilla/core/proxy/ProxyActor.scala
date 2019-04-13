package doradilla.core.proxy

import akka.actor.ActorRef
import doradilla.base.BaseActor
import doradilla.core.fsm.FsmActor.TranslatedActor
import doradilla.core.msg.Job.JobStatus.JobStatus
import doradilla.core.msg.Job.{JobEnd, JobRequest, JobResult, JobStatus}
import doradilla.core.proxy.ProxyActor.{ProxyTaskResult, QueryProxy}

/**
  * For doradilla.proxy in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/30
  */
class ProxyActor(queueActor: ActorRef) extends BaseActor {
  var status: JobStatus = JobStatus.Unknown
  var replyTo: ActorRef = null
  var requestMsgBk: JobRequest = null
  var fsmActor: ActorRef =null
  var result: Option[String] = None
  var translatedActorSeq :Seq[ActorRef] =Seq()

  def handleJobRequest(requestMsg: JobRequest): Unit = {
    replyTo = requestMsg.replyTo
    requestMsgBk =requestMsg
    queueActor ! JobRequest(requestMsg.taskMsg, self, requestMsg.tranActor)
    status = JobStatus.Queued
  }

  def finishTask()={
    fsmActor ! JobEnd(requestMsgBk)
  }

  override def receive: Receive = {
    case jobRequest: JobRequest => handleJobRequest(jobRequest)
    case jobResult:JobResult => result = Some(jobResult.result)
      replyTo ! jobResult
      self ! JobStatus.Finished
    case JobStatus.Scheduled => fsmActor = sender()
      status =JobStatus.Scheduled
    case JobStatus.Finished | JobStatus.Failed | JobStatus.TimeOut =>
      finishTask()
    case msg: JobStatus => status = msg
    case query: QueryProxy => sender() ! ProxyTaskResult(requestMsgBk, status, result, translatedActorSeq, fsmActor )
    case translatedActor: TranslatedActor =>translatedActorSeq = translatedActorSeq :+ translatedActor.child
  }
}

object ProxyActor {

  case class QueryProxy()

  case class ProxyTaskResult(requestMsg: JobRequest, status: JobStatus, result: Option[String], translatedActorSeq: Seq[ActorRef], fsmActor: ActorRef)

}
