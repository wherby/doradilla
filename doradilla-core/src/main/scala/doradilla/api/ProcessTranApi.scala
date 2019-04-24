package doradilla.api

import java.util.UUID

import akka.actor.PoisonPill
import doradilla.core.msg.Job.{JobMsg, JobRequest, JobResult}
import doradilla.tool.job.process.ProcessTranActor
import doradilla.tool.receive.ReceiveActor
import doradilla.tool.receive.ReceiveActor.{FetchResult, ProxyControlMsg}
import doradilla.util.ProcessService.ProcessCallMsg

import scala.concurrent.{ExecutionContext, Future}
import akka.pattern.ask
import akka.util.Timeout

/**
  * For doradilla.api in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/24
  */
trait ProcessTranApi {
  this: SystemApi with DriverApi =>
  val processTranActor = actorSystem.actorOf(ProcessTranActor.processTranActorProps, "defaultProcessTranActor")

  def runProcessCommand(processCallMsg: ProcessCallMsg, timeout: Timeout = longTimeout)(implicit ex: ExecutionContext): Future[JobResult] = {
    val processJob = JobMsg("SimpleProcess", processCallMsg)
    val receiveActor = actorSystem.actorOf(ReceiveActor.receiveActorProps, "Receive" + UUID.randomUUID().toString)
    val processJobRequest = JobRequest(processJob, receiveActor, processTranActor)
    defaultDriver.tell(processJobRequest, receiveActor)
    val result = (receiveActor ? FetchResult()) (timeout).map {
      result =>
        receiveActor ! ProxyControlMsg(PoisonPill)
        receiveActor ! PoisonPill
        result.asInstanceOf[JobResult]
    }
    result
  }
}
