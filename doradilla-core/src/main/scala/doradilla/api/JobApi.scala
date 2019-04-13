package doradilla.api

import java.util.UUID

import akka.actor.{PoisonPill, Props}
import doradilla.core.msg.Job.{JobMsg, JobRequest, JobResult}
import doradilla.tool.receive.ReceiveActor
import doradilla.tool.receive.ReceiveActor.{FetchResult, ProxyControlMsg}
import play.api.libs.json.Json
import akka.pattern.ask
import akka.util.Timeout
import doradilla.tool.job.command.CommandTranActor.CommandRequest
import doradilla.vars.ConstVars

import scala.concurrent.{ExecutionContext, Future}
/**
  * For doradilla.api in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/13
  */
class JobApi {
  val systemApi = JobApi.getSystem()
  implicit val longTimeout = Timeout(ConstVars.longTimeOut)
  def processCommand(command:List[String])(implicit ex:ExecutionContext) ={
    val commandJobMsg =JobMsg("SimpleCommand", Json.toJson(CommandRequest(command)).toString())
    val receiveActor = systemApi.actorSystem.actorOf(Props(new ReceiveActor),"Receive" + UUID.randomUUID().toString)
    val commandJobRequest = JobRequest(commandJobMsg,receiveActor,systemApi.translatedActor)
    systemApi.driver.tell(commandJobRequest, receiveActor)
    val result = (receiveActor ? FetchResult()).map{
      result =>
        receiveActor ! ProxyControlMsg(PoisonPill)
        receiveActor ! PoisonPill
        result.asInstanceOf[JobResult]
    }
    result
  }
}

object JobApi{
  type JobSystem = SystemApi with DriverApi with CommandTranApi
  private var actorSystemOpt:Option[JobSystem] = None

  def getSystem()={
    actorSystemOpt match {
      case Some(actorSystem) => actorSystem
      case _=> new SystemApi with DriverApi with CommandTranApi
    }
  }
}


