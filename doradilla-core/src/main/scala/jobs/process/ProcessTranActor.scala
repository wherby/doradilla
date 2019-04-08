package jobs.process

import akka.actor.ActorRef
import doradilla.base.BaseActor
import doradilla.core.msg.Job.{JobRequest, TranslatedTask, TranslationError, WorkerInfo}
import jobs.process.ProcessTranActor.{ProcessOperation, ProcessRequest, SimpleProcessInit}
import play.api.libs.json.Json

/**
  * For jobs.process in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/7
  */
class ProcessTranActor extends BaseActor{
  implicit val ProcessRequestFormat = Json.format[ProcessRequest]
  def translateProcessRequst(jobRequest: JobRequest) ={
    ProcessOperation.withDefaultName(jobRequest.taskMsg.operation) match {
      case ProcessOperation.SimpleProcess =>
        Json.parse(jobRequest.taskMsg.data).asOpt[ProcessRequest] match {
          case Some(processRequest) =>
            println(WorkerInfo(classOf[ProcessWorkerActor].getName,None,Some(jobRequest.replyTo)))
            sender()! WorkerInfo(classOf[ProcessWorkerActor].getName,None,Some(jobRequest.replyTo))
            sender()! TranslatedTask(SimpleProcessInit(processRequest,jobRequest.replyTo))
          case _=> sender() ! TranslationError(Some(s"Request data failed to process: ${jobRequest.taskMsg.data}"))
        }
      case _=> sender() ! TranslationError(Some(s"Operation name failed to process: ${jobRequest.taskMsg.operation}"))
    }
  }

  override def receive: Receive = {
    case jobRequest: JobRequest => translateProcessRequst(jobRequest)
  }

}

object ProcessTranActor{
  implicit  val processRequestFormat = Json.format[ProcessRequest]
  object ProcessOperation extends Enumeration {
    type ProcessOperation = Value

    val SimpleProcess, Unknown = Value

    def withDefaultName(name: String): Value ={
        values.find(_.toString.toLowerCase == name.toLowerCase).getOrElse(Unknown)
    }
  }

  case class ProcessRequest(cmdWin : List[String],cmdLinux : List[String])

  case class SimpleProcessInit(processRequest: ProcessRequest, replyTo: ActorRef)
}
