package doracore.core.proxy

import akka.actor.{ActorRef, Props}
import doracore.base.BaseActor
import doracore.core.fsm.FsmActor.TranslatedActor
import doracore.core.msg.Job.JobStatus.JobStatus
import doracore.core.msg.Job.{JobEnd, JobRequest, JobResult, JobStatus}
import doracore.core.proxy.ProxyActor.{ProxyTaskResult, QueryProxy}

/**
  * For doradilla.proxy in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/30
  */
class ProxyActor(queueActor: ActorRef) extends BaseActor {
  var status: JobStatus = JobStatus.Unknown
  var replyTo: ActorRef = null
  var requestMsgBk: JobRequest = null
  var fsmActor: ActorRef =null
  var result: Option[Any] = None
  var translatedActorSeq :Seq[ActorRef] =Seq()

  def handleJobRequest(requestMsg: JobRequest): Unit = {
    replyTo = requestMsg.replyTo
    requestMsgBk =requestMsg
    val updatedJobRequest = requestMsg.copy(replyTo=self)
    queueActor ! updatedJobRequest
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
    case query: QueryProxy => sender() ! ProxyTaskResult(requestMsgBk, status, result, translatedActorSeq, fsmActor )
    case translatedActor: TranslatedActor =>translatedActorSeq = translatedActorSeq :+ translatedActor.child
  }
}

object ProxyActor {
  def proxyProps(queue: ActorRef): Props = Props(new ProxyActor(queue))

  case class QueryProxy()

  case class ProxyTaskResult(requestMsg: JobRequest, status: JobStatus, result: Option[Any], translatedActorSeq: Seq[ActorRef], fsmActor: ActorRef)

}
