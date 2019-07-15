package doracore.tool.job.command

import akka.actor.{ActorRef, Props}
import doracore.base.BaseActor
import doracore.core.msg.Job.{JobRequest, WorkerInfo}
import doracore.core.msg.TranslationMsg.{TranslatedTask, TranslationDataError, TranslationOperationError}
import doracore.tool.job.command.CommandTranActor.{CommandOperation, CommandRequest, SimpleCommandInit}
import play.api.libs.json.Json

/**
  * For doradilla.tool.job.command in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/13
  */
class CommandTranActor extends BaseActor {
  implicit val commandRequestFormat = Json.format[CommandRequest]

  def translateCommandRequest(jobRequest: JobRequest) = {
    CommandOperation.withDefaultName(jobRequest.taskMsg.operation) match {
      case CommandOperation.SimpleCommand =>
        Json.parse(jobRequest.taskMsg.data.toString).asOpt[CommandRequest] match {
          case Some(commandRequest) =>
            sender() ! WorkerInfo(classOf[CommandWorkerActor].getName, None, Some(jobRequest.replyTo))
            sender() ! TranslatedTask(SimpleCommandInit(commandRequest, jobRequest.replyTo))
          case _ => sender() ! TranslationDataError(Some(s" ${jobRequest.taskMsg.data}"))
        }
      case _ => sender() ! TranslationOperationError(Some(jobRequest.taskMsg.operation))
    }
  }

  override def receive: Receive = {
    case jobRequest: JobRequest => translateCommandRequest(jobRequest)
  }
}

object CommandTranActor {
  def commandTranProps = Props(new CommandTranActor())

  implicit val commandRequestFormat = Json.format[CommandRequest]

  object CommandOperation extends Enumeration {
    type CommandOperation = Value

    val SimpleCommand, Unknown = Value

    def withDefaultName(name: String): Value = {
      values.find(_.toString.toLowerCase == name.toLowerCase).getOrElse(Unknown)
    }
  }

  case class CommandRequest(command: List[String])

  case class SimpleCommandInit(commandRequest: CommandRequest, repleyTo: ActorRef)

}
