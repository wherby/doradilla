package io.github.wherby.doradilla.back

import java.util.UUID

import akka.actor.{ActorRef, ActorSystem, PoisonPill, Props}
import io.github.wherby.doradilla.conf.{Const, DoraConf}
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings, ClusterSingletonProxy, ClusterSingletonProxySettings}
import akka.event.slf4j.Logger
import akka.util.Timeout
import doradilla.core.driver.DriverActor
import doradilla.core.msg.Job.{JobMsg, JobRequest, JobResult, JobStatus}
import doradilla.tool.job.process.ProcessTranActor
import doradilla.tool.receive.ReceiveActor
import doradilla.tool.receive.ReceiveActor.{FetchResult, ProxyControlMsg}
import doradilla.util.ProcessService.ProcessCallMsg
import doradilla.vars.ConstVars
import play.api.libs.json.JsError
import play.api.libs.json.JsResult.Exception

import scala.concurrent.{ExecutionContext, Future}
import akka.pattern.ask
import io.github.wherby.doradilla.back.BackendServer.proxyProps
import doradilla.core.fsm.FsmActor
import doradilla.core.fsm.FsmActor.RegistToDriver
import doradilla.util.CNaming

/**
  * For io.github.wherby.doradilla.back in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/5/11
  */
object BackendServer {
  var backendServerMap: Map[Int, BackendServer] = Map()
  var nextPort = 0
  lazy val seedPort = DoraConf.config.getInt("dora.clustering.seed-port")

  def startup(portConf: Option[Int] = None): BackendServer = {
    portConf match {
      case Some(port) => backendServerMap.get(port) match {
        case Some(backendServer) => backendServer
        case _ => createBackendServer(Some(port))
      }
      case _ => createBackendServer(portConf)
    }
  }

  private def createBackendServer(portConf: Option[Int]) = {
    val backendServer = new BackendServer()

    def getAvailablePort() = {
      if (nextPort == 0) {
        nextPort = seedPort
      } else {
        nextPort = nextPort + 1
      }
      nextPort
    }

    val port = portConf match {
      case Some(port) => port
      case _ => getAvailablePort
    }
    val clusterName = DoraConf.config.getString("clustering.cluster.name")
    val system = ActorSystem(clusterName, DoraConf.config(port, Const.backendRole))
    setUpClusterSingleton(system, DriverActor.driverActorPropsWithoutFSM(), Const.driverServiceName)
    setUpClusterSingleton(system, ProcessTranActor.processTranActorProps, Const.procssTranServiceName)
    backendServer.actorSystemOpt = Some(system)
    backendServerMap += (port -> backendServer)
    backendServer.getActorProxy(Const.driverServiceName)
    backendServer.getActorProxy(Const.procssTranServiceName)
    backendServer
  }

  def runProcessCommand(processCallMsg: ProcessCallMsg, backendServerOpt: Option[BackendServer] = None, timeout: Timeout = ConstVars.longTimeOut, priority: Option[Int] = None)(implicit ex: ExecutionContext): Future[JobResult] = {
    val backendServer = backendServerOpt match {
      case Some(backendServer) => backendServer
      case _ => startup(Some(seedPort))
    }
    (backendServer.getActorProxy(Const.driverServiceName), backendServer.getActorProxy(Const.procssTranServiceName)) match {
      case (Some(driverService), Some(processTranService)) =>
        val processJob = JobMsg("SimpleProcess", processCallMsg)
        val actorSystem = backendServer.actorSystemOpt.get
        val receiveActor = actorSystem.actorOf(ReceiveActor.receiveActorProps, "Receive" + UUID.randomUUID().toString)
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

  def setUpClusterSingleton(system: ActorSystem, props: Props, name: String): ActorRef = {
    system.actorOf(ClusterSingletonManager.props(
      singletonProps = props,
      terminationMessage = PoisonPill,
      settings = ClusterSingletonManagerSettings(system).withRole(Const.backendRole)),
      name = name)
  }


  // #proxy
  def proxyProps(system: ActorSystem, name: String): Props = ClusterSingletonProxy.props(
    settings = ClusterSingletonProxySettings(system).withRole(Const.backendRole),
    singletonManagerPath = s"/user/$name")

}

class BackendServer {
  var actorSystemOpt: Option[ActorSystem] = None
  var actorMap: Map[String, ActorRef] = Map()

  def getActorProxy(actorName: String): Option[ActorRef] = {
    actorMap.get(actorName) match {
      case Some(actorProxy) => Some(actorProxy)
      case _ => val actorProxyOpt = actorSystemOpt.map {
        actorSystem => actorSystem.actorOf(proxyProps(actorSystem, actorName), CNaming.timebasedName(actorName + "Proxy"))
      }
        actorProxyOpt.map {
          actorProxy => actorMap += (actorName -> actorProxy)
        }
        actorProxyOpt
    }
  }

  def registFSMActor(): Unit = {
    actorSystemOpt.map {
      actorSystem =>
        val fsmActorName = CNaming.timebasedName("FsmActor")
        val fsmActor: ActorRef = actorSystem.actorOf(FsmActor.fsmActorProps, fsmActorName)
        this.getActorProxy(Const.driverServiceName).map {
          driverProxy =>
            println(driverProxy)
            driverProxy.tell(RegistToDriver(fsmActor), fsmActor)
        }
        actorMap += (fsmActorName -> fsmActor)
    }
  }
}

