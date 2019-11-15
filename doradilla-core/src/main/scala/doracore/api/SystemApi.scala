package doracore.api

import akka.actor.ActorSystem
import akka.util.Timeout
import doracore.util.{CNaming, ConfigService, DoraCoreConfig}
import doracore.vars.ConstVars

import scala.concurrent.ExecutionContextExecutor


/**
  * For doradilla.api in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/9
  */
class SystemApi(systemOpt: Option[ActorSystem] = None) extends GetBlockIOExcutor {
  val longTimeout = Timeout(ConstVars.longTimeOut)

  override def getBlockDispatcher(): ExecutionContextExecutor = {
    actorSystem.dispatchers.hasDispatcher(ConstVars.blockDispatcherName) match {
      case true => actorSystem.dispatchers.lookup(ConstVars.blockDispatcherName)
      case _ => actorSystem.dispatcher
    }
  }

  lazy val doradillaConfig = DoraCoreConfig.getConfig()

  def createDoradillaSystem: ActorSystem = {
    val actorSystemName = ConfigService.getStringOpt(doradillaConfig, "doradillaSystem").getOrElse(CNaming.timebasedName( "diradilla"))
    ActorSystem(actorSystemName, doradillaConfig)
  }

  val actorSystem: ActorSystem = systemOpt match {
    case Some(system) => system
    case _ => createDoradillaSystem
  }
}
