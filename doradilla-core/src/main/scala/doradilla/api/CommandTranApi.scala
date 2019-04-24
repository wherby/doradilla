package doradilla.api

import java.util.UUID
import akka.actor.PoisonPill
import doradilla.core.msg.Job.{JobMsg, JobRequest, JobResult}
import doradilla.tool.job.command.CommandTranActor
import doradilla.tool.job.command.CommandTranActor.CommandRequest
import doradilla.tool.receive.ReceiveActor
import doradilla.tool.receive.ReceiveActor.{FetchResult, ProxyControlMsg}
import play.api.libs.json.Json
import scala.concurrent.{ExecutionContext, Future}
import akka.pattern.ask
import akka.util.Timeout

/**
  * For doradilla.api in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/13
  */
trait CommandTranApi {
  this: SystemApi with DriverApi=>
  val commandTranslatedActor = actorSystem.actorOf(CommandTranActor.commandTranProps, "CommandTran")

  def processCommand(command: List[String],timeout: Timeout = longTimeout)(implicit ex: ExecutionContext): Future[JobResult] = {
    val commandJobMsg = JobMsg("SimpleCommand", Json.toJson(CommandRequest(command)).toString())
    val receiveActor = actorSystem.actorOf(ReceiveActor.receiveActorProps, "Receive" + UUID.randomUUID().toString)
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
