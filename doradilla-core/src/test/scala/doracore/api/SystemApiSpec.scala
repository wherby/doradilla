package doracore.api

import akka.actor.ActorSystem
import doracore.ActorTestClass
import doracore.util.CNaming

/**
  * For doradilla.api in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/9
  */
class SystemApiSpec extends ActorTestClass{
  "SystemApi" must{
    "Return a new actorSytem with doradillaSystem prefix " in {
      val system = new SystemApi()
      system.actorSystem.name should startWith ("doradilla")
    }
    "Return a existed actorSystem when system pass in " in {
      val system = ActorSystem(CNaming.timebasedName( "SystemApiSpecTest"))
      val systemApi = new SystemApi(Some(system))
      systemApi.actorSystem.name should startWith("SystemApiSpecTest")
    }
  }
}
