package doradilla.api

import akka.actor.ActorSystem
import akka.util.Timeout
import doradilla.util.{CNaming, ConfigService, DoraConfig}
import doradilla.vars.ConstVars


/**
  * For doradilla.api in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/9
  */
class SystemApi(systemOpt: Option[ActorSystem] = None) {
  val longTimeout = Timeout(ConstVars.longTimeOut)



  lazy val doradillaConfig = DoraConfig.getConfig()

  def createDoradillaSystem: ActorSystem = {
    val actorSystemName = ConfigService.getStringOpt(doradillaConfig, "doradillaSystem").getOrElse(CNaming.timebasedName( "diradilla"))
    ActorSystem(actorSystemName, doradillaConfig)
  }

  val actorSystem: ActorSystem = systemOpt match {
    case Some(system) => system
    case _ => createDoradillaSystem
  }
}
