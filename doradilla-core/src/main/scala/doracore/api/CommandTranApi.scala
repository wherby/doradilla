package doracore.api


import doracore.core.msg.Job.{JobMsg, JobRequest, JobResult}
import doracore.tool.job.command.CommandTranActor
import doracore.tool.job.command.CommandTranActor.CommandRequest
import doracore.tool.receive.ReceiveActor
import play.api.libs.json.Json
import scala.concurrent.{ExecutionContext, Future}
import akka.util.Timeout
import doracore.util.CNaming

/**
  * For doradilla.api in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/13
  */
trait CommandTranApi extends  AskProcessResult{
  this: SystemApi with DriverApi=>
  val commandTranslatedActor = actorSystem.actorOf(CommandTranActor.commandTranProps, CNaming.timebasedName("CommandTran"))

  def processCommand(command: List[String],timeout: Timeout = longTimeout)(implicit ex: ExecutionContext): Future[JobResult] = {
    val commandJobMsg = JobMsg("SimpleCommand", Json.toJson(CommandRequest(command)).toString())
    val receiveActor = actorSystem.actorOf(ReceiveActor.receiveActorProps, CNaming.timebasedName("Receive"))
    val commandJobRequest = JobRequest(commandJobMsg, receiveActor, commandTranslatedActor)
    getProcessCommandFutureResult(commandJobRequest, defaultDriver, receiveActor,timeout)
  }
}


