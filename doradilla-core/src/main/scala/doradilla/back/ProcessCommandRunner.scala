package doradilla.back

import akka.actor.{ActorRef, PoisonPill}
import akka.event.slf4j.Logger
import akka.util.Timeout
import doracore.core.msg.Job._
import doracore.tool.receive.ReceiveActor
import doracore.tool.receive.ReceiveActor.{FetchResult, ProxyControlMsg, QueryResult}
import doracore.util.CNaming
import doracore.util.ProcessService.ProcessCallMsg
import doracore.vars.ConstVars
import play.api.libs.json.JsError
import play.api.libs.json.JsResult.Exception
import akka.pattern.ask
import doradilla.back.BatchProcessActor.{BatchJobResult, BatchProcessJob}
import doradilla.conf.Const

import scala.concurrent.{ExecutionContext, Future}

/**
  * For io.github.wherby.doradilla.back in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/7/14
  */
trait ProcessCommandRunner {
  this: BackendServer.type =>

  def runProcessCommand(processCallMsg: ProcessCallMsg, backendServerOpt: Option[BackendServer] = None, timeout: Timeout = ConstVars.longTimeOut, priority: Option[Int] = None)(implicit ex: ExecutionContext): Future[JobResult] = {
    val backendServer = backendServerOpt match {
      case Some(backendServer) => backendServer
      case _ => startup(Some(seedPort))
    }
    (backendServer.getActorProxy(Const.driverServiceName), backendServer.getActorProxy(Const.procssTranServiceName)) match {
      case (Some(driverService), Some(processTranService)) =>
        val processJob = JobMsg("SimpleProcess", processCallMsg)
        val actorSystem = backendServer.actorSystemOpt.get
        val receiveActor = actorSystem.actorOf(ReceiveActor.receiveActorProps, CNaming.timebasedName("Receive"))
        val processJobRequest = JobRequest(processJob, receiveActor, processTranService, priority)
        driverService.tell(processJobRequest, receiveActor)
        val result = (receiveActor ? FetchResult()) (timeout).map {
          result =>
            receiveActor ! ProxyControlMsg(PoisonPill)
            receiveActor ! PoisonPill
            result.asInstanceOf[JobResult]
        }
        result
      case _ => {
        Logger.apply(this.getClass.getName).info(backendServer.actorMap.toString())
        Future(JobResult(JobStatus.Failed, new Exception(JsError("Can't get service"))))
      }
    }
  }

  def startProcessCommand(processCallMsg: ProcessCallMsg, backendServerOpt: Option[BackendServer] = None, priority: Option[Int] = None)(implicit ex: ExecutionContext): Option[ActorRef] = {
    val backendServer = backendServerOpt match {
      case Some(backendServer) => backendServer
      case _ => startup(Some(seedPort))
    }
    (backendServer.getActorProxy(Const.driverServiceName), backendServer.getActorProxy(Const.procssTranServiceName)) match {
      case (Some(driverService), Some(processTranService)) =>
        val processJob = JobMsg("SimpleProcess", processCallMsg)
        val actorSystem = backendServer.actorSystemOpt.get
        val receiveActor = actorSystem.actorOf(ReceiveActor.receiveActorProps, CNaming.timebasedName("Receive"))
        val processJobRequest = JobRequest(processJob, receiveActor, processTranService, priority)
        driverService.tell(processJobRequest, receiveActor)
        receiveActor ! FetchResult()
        Some(receiveActor)
      case _ => None
    }
  }

  def queryProcessResult(receiveActor: ActorRef, timeout: Timeout = ConstVars.longTimeOut)(implicit ex: ExecutionContext): Future[Option[JobResult]] = {
    (receiveActor ? QueryResult()) (timeout).map {
      resultOpt =>
        resultOpt.asInstanceOf[Option[JobResult]] match {
          case Some(result) => {
            receiveActor ! ProxyControlMsg(PoisonPill)
            receiveActor ! PoisonPill
            Some(result)
          }
          case _ => None
        }
    }
  }

  def startProcessBatchCommand(batchRequests: Seq[ProcessCallMsg],
                               backendServerOpt: Option[BackendServer] = None,
                               priority: Option[Int] = None, jobMetaOpt: Option[JobMeta]= None)(implicit ex: ExecutionContext): Option[ActorRef] = {
    val backendServer = backendServerOpt match {
      case Some(backendServer) => backendServer
      case _ => startup(Some(seedPort))
    }
    (backendServer.getActorProxy(Const.driverServiceName), backendServer.getActorProxy(Const.procssTranServiceName)) match {
      case (Some(driverService), Some(processTranService)) =>
        val actorSystem = backendServer.actorSystemOpt.get
        val receiveActor = actorSystem.actorOf(BatchProcessActor.batchProcessActorProp(), CNaming.timebasedName("BatchProcessActor"))
        val batchProcessJobRequest = BatchProcessJob(batchRequests, driverService, processTranService, priority,jobMetaOpt)
        receiveActor ! batchProcessJobRequest
        Some(receiveActor)
      case _ => None
    }
  }

  def queryBatchProcessResult(batchProcessActor: ActorRef, timeout: Timeout = ConstVars.timeout1S)(implicit ex: ExecutionContext): Future[BatchJobResult] = {
    (batchProcessActor ? QueryResult()) (timeout).map {
      resultOpt =>
        resultOpt.asInstanceOf[BatchJobResult]
    }
  }
}
