package doracore.tool.job.process

import akka.actor.{ActorRef, Props}
import doracore.base.BaseActor
import doracore.core.msg.Job.{JobRequest, WorkerInfo}
import doracore.core.msg.TranslationMsg.{TranslatedTask, TranslationDataError, TranslationOperationError}
import doracore.tool.job.process.ProcessTranActor.{ProcessOperation, SimpleProcessInit}
import doracore.util.ProcessService.ProcessCallMsg


/**
  * For doradilla.tool.job.process in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/22
  */
class ProcessTranActor extends BaseActor{
  def translateProcessRequest(jobRequest: JobRequest) ={
    ProcessOperation.withDefaultName(jobRequest.taskMsg.operation) match {
      case ProcessOperation.SimpleProcess =>
        try{
          val msg = jobRequest.taskMsg.data.asInstanceOf[ProcessCallMsg]
          sender()! WorkerInfo(classOf[ProcessWorkerActor].getName,None,Some(jobRequest.replyTo))
          sender() ! TranslatedTask(SimpleProcessInit(msg,jobRequest.replyTo))
        }catch{
          case _:Throwable => sender() ! TranslationDataError(Some(s"${jobRequest.taskMsg.data}"))
        }
      case _=> sender() ! TranslationOperationError(Some(jobRequest.taskMsg.operation))
    }
  }

  override def receive: Receive = {
    case jobRequest: JobRequest=> translateProcessRequest(jobRequest)
  }
}


object ProcessTranActor{
  val processTranActorProps = Props(new ProcessTranActor())

  object ProcessOperation extends  Enumeration{
    type ProcessOperation = Value

    val SimpleProcess, Unknown = Value

    def withDefaultName(name: String): Value = {
      values.find(_.toString.toLowerCase == name.toLowerCase).getOrElse(Unknown)
    }
  }

  case class SimpleProcessInit(processCallMsg: ProcessCallMsg, replyTo: ActorRef)
}