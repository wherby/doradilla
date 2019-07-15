package doracore.api

import akka.actor.PoisonPill
import doracore.core.msg.Job.{JobMsg, JobRequest, JobResult}
import doracore.tool.job.command.CommandTranActor
import doracore.tool.job.command.CommandTranActor.CommandRequest
import doracore.tool.receive.ReceiveActor
import doracore.tool.receive.ReceiveActor.{FetchResult, ProxyControlMsg}
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}
import akka.pattern.ask
import akka.util.Timeout
import doracore.util.CNaming

/**
  * For doradilla.api in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/13
  */
trait CommandTranApi {
  this: SystemApi with DriverApi=>
  val commandTranslatedActor = actorSystem.actorOf(CommandTranActor.commandTranProps, CNaming.timebasedName("CommandTran"))

  def processCommand(command: List[String],timeout: Timeout = longTimeout)(implicit ex: ExecutionContext): Future[JobResult] = {
    val commandJobMsg = JobMsg("SimpleCommand", Json.toJson(CommandRequest(command)).toString())
    val receiveActor = actorSystem.actorOf(ReceiveActor.receiveActorProps, CNaming.timebasedName("Receive"))
    val commandJobRequest = JobRequest(commandJobMsg, receiveActor, commandTranslatedActor)
    defaultDriver.tell(commandJobRequest, receiveActor)
    val result = (receiveActor ? FetchResult())(timeout).map {
      result =>
        receiveActor ! ProxyControlMsg(PoisonPill)
        receiveActor ! PoisonPill
        result.asInstanceOf[JobResult]
    }
    result
  }
}
