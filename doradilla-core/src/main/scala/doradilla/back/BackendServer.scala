package doradilla.back

import akka.actor.{ActorRef, ActorSystem, PoisonPill, Props}
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings, ClusterSingletonProxy, ClusterSingletonProxySettings}
import doracore.core.driver.DriverActor
import doracore.tool.job.process.ProcessTranActor
import doracore.core.fsm.FsmActor
import doracore.core.fsm.FsmActor.RegistToDriver
import doracore.util.{AppDebugger, CNaming, ConfigService}
import doradilla.conf.{Const, DoraConf}
import BackendServer.proxyProps
import akka.event.slf4j.Logger
import com.typesafe.config.Config
import doracore.api.JobApi
import doracore.tool.query.QueryActor
import doracore.vars.ConstVars


/**
 * For io.github.wherby.doradilla.back in Doradilla
 * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/5/11
 */
object BackendServer extends ProcessCommandRunner {
  var backendServerOpt :Option[BackendServer] =None
  var backendServerMap: Map[Int, BackendServer] = Map()
  var namedJobApiMap: Map[String, JobApi] = Map()
  lazy val seedPort = ConfigService.getIntOpt(DoraConf.config, "cluster-setting.seed-port").getOrElse(ConstVars.DoraPort)
  var nextPort = seedPort


  def getBackendServer()={
    BackendServer.backendServerMap.headOption.map(_._2) match {
      case Some(backendServer) => backendServer
      case _ =>
        startup(Some(ConstVars.DoraPort))
    }
  }

  override def getActorSystem(): ActorSystem = {
    if (BackendServer.backendServerMap.headOption == None) {
      AppDebugger.logInfo("create new server ", Some("getActorSystem"))
      createBackendServer(Some(ConstVars.DoraPort))
    }
    BackendServer.backendServerMap.head._2.actorSystemOpt.get
  }

  def startup(portConf: Option[Int] = None, systemConfigOpt: Option[Config] = None): BackendServer = {
    portConf match {
      case Some(port) => backendServerMap.get(port) match {
        case Some(backendServer) => backendServer
        case _ =>
          AppDebugger.logInfo(s"Start new server $portConf , $systemConfigOpt" , Some("start up"))
          createBackendServer(Some(port), systemConfigOpt)
      }
      case _ =>
        AppDebugger.logInfo(s"Start new server $portConf , $systemConfigOpt" , Some("start up"))
        createBackendServer(portConf)
    }
  }

  def createBackendServer(portConf: Option[Int], systemConfigOpt: Option[Config] = None) :BackendServer = {
    backendServerOpt match {
      case Some(backendServer) => backendServer
      case _=>val backendServer = new BackendServer()
        val port = portConf.getOrElse(ConstVars.DoraPort)
        val clusterName = ConfigService.getStringOpt(DoraConf.config, "cluster-setting.cluster-name").getOrElse("clustering-cluster")
        val systemConfig = systemConfigOpt.getOrElse(DoraConf.config(port, Const.backendRole))
        val system =  ActorSystem(clusterName, systemConfig)
        backendServer.actorSystemOpt = Some(system)
        backendServerMap += (port -> backendServer)

        setupSingletonProxyActor(backendServer, system)
        backendServer
    }
  }


  /**
   * @Description: Setup singleton actor for cluster, and set singleton proxy in the system  and register to actorMap for later use.
   * @Param:
   * @return:
   */
  private def setupSingletonProxyActor(backendServer: BackendServer, system: ActorSystem) = {
    setUpClusterSingleton(system, DriverActor.driverActorPropsWithoutFSM(), Const.driverServiceName)
    setUpClusterSingleton(system, ProcessTranActor.processTranActorProps, Const.procssTranServiceName)
    setUpClusterSingleton(system, QueryActor.queryActorProps, Const.queryService)
    backendServer.getActorProxy(Const.driverServiceName)
    backendServer.getActorProxy(Const.procssTranServiceName)
  }

  def setUpClusterSingleton(system: ActorSystem, props: Props, name: String): ActorRef = {
    system.actorOf(ClusterSingletonManager.props(
      singletonProps = props,
      terminationMessage = PoisonPill,
      settings = ClusterSingletonManagerSettings(system).withRole(Const.backendRole)),
      name = name)
  }


  /**
   * Set singleton proxy
   *
   * @return the Props of singlenton proxy
   */
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
    val fsmNumber = ConfigService.getStringOpt(DoraConf.config, "fsmNumber").getOrElse("1").toInt
    (0 until fsmNumber).map {
      _ =>
        actorSystemOpt.map {
          actorSystem =>
            val fsmActorName = CNaming.timebasedName("FsmActor")
            val fsmActor: ActorRef = actorSystem.actorOf(FsmActor.fsmActorProps, fsmActorName)
            this.getActorProxy(Const.driverServiceName).map {
              driverProxy =>
                driverProxy.tell(RegistToDriver(fsmActor), fsmActor)
            }
            actorMap += (fsmActorName -> fsmActor)
            Logger.apply(this.getClass.getName).info(s"ActorMap are $actorMap")
        }
    }
  }
}

