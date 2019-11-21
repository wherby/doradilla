package doracore.api

import akka.actor.ActorSystem
import akka.util.Timeout
import doracore.util.{CNaming, ConfigService, DoraCoreConfig}
import doracore.vars.ConstVars



/**
  * For doradilla.api in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/9
  */
class SystemApi(systemOpt: Option[ActorSystem] = None) extends GetBlockIOExcutor with ActorSystemApi {
  val longTimeout = Timeout(ConstVars.longTimeOut)

  lazy val doradillaConfig = DoraCoreConfig.getConfig()

  def createDoradillaSystem: ActorSystem = {
    val actorSystemName = ConfigService.getStringOpt(doradillaConfig, "doradillaSystem").getOrElse(CNaming.timebasedName( "diradilla"))
    ActorSystem(actorSystemName, doradillaConfig)
  }

  override def getActorSystem(): ActorSystem = {
    actorSystem
  }

  val actorSystem: ActorSystem = systemOpt match {
    case Some(system) => system
    case _ => createDoradillaSystem
  }
}
