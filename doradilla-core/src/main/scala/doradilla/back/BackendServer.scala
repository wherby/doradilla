package doradilla.back

import akka.actor.{ActorRef, ActorSystem, PoisonPill, Props}
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings, ClusterSingletonProxy, ClusterSingletonProxySettings}
import doracore.core.driver.DriverActor
import doracore.tool.job.process.ProcessTranActor
import doracore.core.fsm.FsmActor
import doracore.core.fsm.FsmActor.RegistToDriver
import doracore.util.{CNaming, ConfigService}
import doradilla.conf.{Const, DoraConf}
import BackendServer.proxyProps

/**
  * For io.github.wherby.doradilla.back in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/5/11
  */
object BackendServer extends ProcessCommandRunner {
  var backendServerMap: Map[Int, BackendServer] = Map()
  var nextPort = 0
  lazy val seedPort = DoraConf.config.getInt("clustering.seed-port")

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
      case Some(port) =>
        nextPort =port +1
        port
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
    val fsmNumber = ConfigService.getStringOpt( DoraConf.config, "fsmNumber").getOrElse("1").toInt
    (0 until fsmNumber).map{
      _=>
        actorSystemOpt.map {
          actorSystem =>
            val fsmActorName = CNaming.timebasedName("FsmActor")
            val fsmActor: ActorRef = actorSystem.actorOf(FsmActor.fsmActorProps, fsmActorName)
            this.getActorProxy(Const.driverServiceName).map {
              driverProxy =>
                //println(driverProxy)
                driverProxy.tell(RegistToDriver(fsmActor), fsmActor)
            }
            actorMap += (fsmActorName -> fsmActor)
        }
    }
  }
}

