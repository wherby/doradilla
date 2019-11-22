package doracore.api

import doracore.core.msg.Job.{JobMsg, JobRequest, JobResult}
import doracore.tool.job.process.ProcessTranActor
import doracore.tool.receive.ReceiveActor
import doracore.util.ProcessService.ProcessCallMsg

import scala.concurrent.{ExecutionContext, Future}
import akka.util.Timeout
import doracore.util.CNaming

/**
  * For doradilla.api in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/24
  */
trait ProcessTranApi extends AskProcessResult{
  this: SystemApi with DriverApi =>
  val processTranActor = actorSystem.actorOf(ProcessTranActor.processTranActorProps, CNaming.timebasedName( "defaultProcessTranActor"))

  def runProcessCommand(processCallMsg: ProcessCallMsg, timeout: Timeout = longTimeout)(implicit ex: ExecutionContext): Future[JobResult] = {
    val processJob = JobMsg("SimpleProcess", processCallMsg)
    val receiveActor = actorSystem.actorOf(ReceiveActor.receiveActorProps, CNaming.timebasedName( "Receive"))
    val processJobRequest = JobRequest(processJob, receiveActor, processTranActor)
    getProcessCommandFutureResult(processJobRequest, defaultDriver, receiveActor,timeout)
  }
}
