package doradilla.back

import akka.actor.{ActorRef, ActorSystem}
import akka.event.slf4j.Logger
import akka.util.Timeout
import doracore.core.msg.Job._
import doracore.tool.receive.ReceiveActor
import doracore.tool.receive.ReceiveActor.{FetchResult}
import doracore.util.CNaming
import doracore.vars.ConstVars
import play.api.libs.json.JsError
import play.api.libs.json.JsResult.Exception
import doracore.api.{ActorSystemApi, AskProcessResult, GetBlockIOExcutor}
import doradilla.back.batch.BatchProcessor
import doradilla.conf.Const
import scala.concurrent.{ExecutionContext, Future}

/**
  * For io.github.wherby.doradilla.back in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/7/14
  */
trait ProcessCommandRunner extends AskProcessResult with GetBlockIOExcutor with ActorSystemApi with BatchProcessor with NamedJobRunner {
  this: BackendServer.type =>
  def runProcessCommand(processJob: JobMsg,
                        backendServerOpt: Option[BackendServer] = None,
                        timeout: Timeout = ConstVars.longTimeOut,
                        priority: Option[Int] = None,
                        metaOpt:Option[JobMeta] =None)(implicit ex: ExecutionContext): Future[JobResult] = {
    val backendServer = getBackendServer()
    val resultOpt = for (driverService <- backendServer.getActorProxy(Const.driverServiceName);
                         processTranService <- backendServer.getActorProxy(Const.procssTranServiceName))
      yield {
        val actorSystem = backendServer.actorSystemOpt.get
        val receiveActor = actorSystem.actorOf(ReceiveActor.receiveActorProps, CNaming.timebasedName("Receive"))
        val processJobRequest = JobRequest(processJob, receiveActor, processTranService, priority,metaOpt)
        getProcessCommandFutureResult(processJobRequest, driverService, receiveActor,timeout)
      }
    resultOpt.getOrElse(Future(JobResult(JobStatus.Failed, new Exception(JsError("Can't get service")))))
  }



  def startProcessCommand(processJob: JobMsg, backendServerOpt: Option[BackendServer] = None, priority: Option[Int] = None)(implicit ex: ExecutionContext): Option[ActorRef] = {
    val backendServer = getBackendServer()
    for (driverService <- backendServer.getActorProxy(Const.driverServiceName);
         processTranService <- backendServer.getActorProxy(Const.procssTranServiceName))
      yield {
        val actorSystem = backendServer.actorSystemOpt.get
        val receiveActor = actorSystem.actorOf(ReceiveActor.receiveActorProps, CNaming.timebasedName("Receive"))
        val processJobRequest = JobRequest(processJob, receiveActor, processTranService, priority)
        driverService.tell(processJobRequest, receiveActor)
        receiveActor ! FetchResult()
        receiveActor
      }
  }

  def queryProcessResult(receiveActor: ActorRef, timeout: Timeout = ConstVars.longTimeOut): Future[JobResult] = {
    getResult(receiveActor,timeout)
  }


}
