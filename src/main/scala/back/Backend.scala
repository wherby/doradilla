package back

import java.util.UUID

import akka.actor.{ActorRef, ActorSystem, PoisonPill, Props}
import conf.{Const, DoraConf}
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings, ClusterSingletonProxy, ClusterSingletonProxySettings}
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
import scala.util.Random
import akka.pattern.ask

/**
  * For back in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/5/11
  */
object Backend {
  var actorSystemOpt : Option[ActorSystem] = None
  var actorMap: Map[String,ActorRef] = Map()

  def startup(portConf: Option[Int]):Option[ActorSystem] ={
    actorSystemOpt match {
      case Some(actorSystem) => Some(actorSystem)
      case _=>
        val port = portConf match {
          case Some(port) => port
          case _=> Random.nextInt(1000) + 2000
        }
        val clusterName = DoraConf.config.getString("clustering.cluster.name")
        val system = ActorSystem(clusterName, DoraConf.config(port, Const.backendRole))
        setUpClusterSiglenton(system,DriverActor.driverActorPropsWithoutFSM(),Const.driverServiceName)
        setUpClusterSiglenton(system, ProcessTranActor.processTranActorProps,Const.procssTranServiceName)
        actorSystemOpt = Some(system)
        actorSystemOpt
    }
  }

  def runProcessCommand(processCallMsg: ProcessCallMsg, timeout: Timeout = ConstVars.longTimeOut)(implicit ex: ExecutionContext): Future[JobResult] = {
    (actorMap.get(Const.driverServiceName), actorMap.get(Const.procssTranServiceName)) match {
      case (Some(dirverSercice), Some(processTranService)) =>
        val processJob = JobMsg("SimpleProcess", processCallMsg)
        val actorSystem = startup(Some(2000)).get
        val receiveActor = actorSystem.actorOf(ReceiveActor.receiveActorProps, "Receive" + UUID.randomUUID().toString)
        val processJobRequest = JobRequest(processJob, receiveActor, processTranService)
        dirverSercice.tell(processJobRequest, receiveActor)
        val result = (receiveActor ? FetchResult()) (timeout).map {
          result =>
            receiveActor ! ProxyControlMsg(PoisonPill)
            receiveActor ! PoisonPill
            result.asInstanceOf[JobResult]
        }
        result
      case _=>Future(JobResult(JobStatus.Failed, new Exception(JsError("Can't get service"))))
    }
  }

  def getActorProxy(actorName: String):Option[ActorRef] = {
    actorMap.get(actorName) match {
      case Some(actorProxy) => Some(actorProxy)
      case _=>  val actorProxyOpt = actorSystemOpt.map{
        actorSystem=>actorSystem.actorOf(proxyProps(actorSystem,actorName),actorName + "Proxy" + UUID.randomUUID().toString)
      }
        actorProxyOpt.map{
          actorProxy=> actorMap +=(actorName -> actorProxy)
        }
        actorProxyOpt
    }
  }


  def setUpClusterSiglenton(system: ActorSystem, props:Props,name:String): ActorRef ={
    system.actorOf(ClusterSingletonManager.props(
      singletonProps = props,
      terminationMessage = PoisonPill,
      settings = ClusterSingletonManagerSettings(system).withRole(Const.backendRole)),
      name = name)
  }


  // #proxy
  def proxyProps(system: ActorSystem, name:String):Props = ClusterSingletonProxy.props(
    settings = ClusterSingletonProxySettings(system).withRole(Const.backendRole),
    singletonManagerPath = s"/user/$name")

}

