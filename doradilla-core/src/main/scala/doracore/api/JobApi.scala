package doracore.api

import akka.actor.ActorSystem


/**
  * For doradilla.api in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/13
  */
class JobApi extends SystemApi with DriverApi with CommandTranApi with ProcessTranApi {
  val systemApi = JobApi.getSystem()

}

object JobApi {
  type JobSystem = SystemApi
  var actorSystemOpt: Option[JobSystem] = None

  def getSystem(systemOpt: Option[ActorSystem] = None) = {
    actorSystemOpt match {
      case Some(actorSystem) => actorSystem
      case _ => new SystemApi(systemOpt) with DriverApi with CommandTranApi with ProcessTranApi
    }
  }
}


