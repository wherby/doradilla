package doradilla.api

import java.util.UUID
import akka.actor.ActorSystem
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}
import doradilla.util.ConfigService
import doradilla.vars.ConstVars


/**
  * For doradilla.api in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/9
  */
class SystemApi(systemOpt: Option[ActorSystem] = None) {
  val longTimeout = Timeout(ConstVars.longTimeOut)

  def getConfig(): Config = {
    val config = ConfigFactory.load()
    ConfigFactory.load("doradilla").withFallback(config).resolve()
  }

  lazy val doradillaConfig = getConfig()

  def createDoradillaSystem: ActorSystem = {
    val actorSystemName = ConfigService.getStringOpt(doradillaConfig, "doradillaSystem").getOrElse("diradilla" + UUID.randomUUID().toString)
    ActorSystem(actorSystemName, doradillaConfig)
  }

  val actorSystem: ActorSystem = systemOpt match {
    case Some(system) => system
    case _ => createDoradillaSystem
  }
}
