package doracore.tool.job.process

import akka.actor.{ActorRef, Props}
import doracore.base.BaseActor
import doracore.core.msg.Job.{JobRequest, WorkerInfo}
import doracore.core.msg.TranslationMsg.{TranslatedTask, TranslationDataError, TranslationOperationError}
import doracore.tool.job.process.ProcessTranActor.{ProcessOperation, SimpleProcessFutureInit, SimpleProcessInit}
import doracore.util.ProcessService.ProcessCallMsg


/**
  * For doradilla.tool.job.process in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/22
  */
class ProcessTranActor extends BaseActor{
  def translateProcessRequest(jobRequest: JobRequest) ={
    ProcessOperation.withDefaultName(jobRequest.taskMsg.operation) match {
      case ProcessOperation.SimpleProcess =>
        safeTranslate(jobRequest,processSimpleProcess)
      case ProcessOperation.SimpleProcessFuture =>
        safeTranslate(jobRequest,processSimpleProcessFuture)
      case _=> sender() ! TranslationOperationError(Some(jobRequest.taskMsg.operation))
    }
  }

  private def safeTranslate(jobRequest: JobRequest,transFun: JobRequest => Unit) = {
    try {
      log.debug(s"Start running reqeust for: $jobRequest")
      transFun(jobRequest)
    } catch {
      case _: Throwable => sender() ! TranslationDataError(Some(s"${jobRequest.taskMsg.data}"))
    }
  }

  private def processSimpleProcessFuture(jobRequest: JobRequest) = {
    val msg = jobRequest.taskMsg.data.asInstanceOf[ProcessCallMsg]
    sender() ! WorkerInfo(classOf[ProcessWorkerActor].getName, None, Some(jobRequest.replyTo))
    sender() ! TranslatedTask(SimpleProcessFutureInit(msg, jobRequest.replyTo))
  }

  private def processSimpleProcess(jobRequest: JobRequest) = {
    val msg = jobRequest.taskMsg.data.asInstanceOf[ProcessCallMsg]
    sender() ! WorkerInfo(classOf[ProcessWorkerActor].getName, None, Some(jobRequest.replyTo))
    sender() ! TranslatedTask(SimpleProcessInit(msg, jobRequest.replyTo))
  }

  override def receive: Receive = {
    case jobRequest: JobRequest=> translateProcessRequest(jobRequest)
  }
}


object ProcessTranActor{
  val processTranActorProps = Props(new ProcessTranActor())

  object ProcessOperation extends  Enumeration{
    type ProcessOperation = Value

    val SimpleProcess, SimpleProcessFuture, Unknown = Value

    def withDefaultName(name: String): Value = {
      values.find(_.toString.toLowerCase == name.toLowerCase).getOrElse(Unknown)
    }
  }

  case class SimpleProcessInit(processCallMsg: ProcessCallMsg, replyTo: ActorRef)

  case class SimpleProcessFutureInit(processCallMsg: ProcessCallMsg, replyTo: ActorRef)
}