package doracore.api

import akka.actor.{ActorRef}
import doracore.core.driver.DriverActor
import doracore.util.{CNaming, ConfigService}

/**
  * For doradilla.api in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/9
  */
trait DriverApi {
  this: SystemApi =>
  def createDriver(queueActorOpt: Option[ActorRef] = None, driverNameOpt: Option[String] = None): ActorRef = {
    val driverName = driverNameOpt match {
      case Some(driverName) => driverName
      case _ => ConfigService.getStringOpt(doradillaConfig, "driverPrefix").getOrElse(CNaming.timebasedName( "driver"))
    }
    actorSystem.actorOf(DriverActor.driverActorProps(queueActorOpt), driverName)
  }

  var queueActorSet:Option[ActorRef] = None

  lazy val defaultDriver: ActorRef =createDriver(queueActorSet)
}
