package doradilla.api

import doradilla.ActorTestClass
import doradilla.core.queue.QueueActor

/**
  * For doradilla.api in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/13
  */
class DriverApiSpec extends ActorTestClass{
  "Driver Api" must{
    "Return driver actor " in{
      val system = new SystemApi()with DriverApi
      system.defaultDriver.toString() should include ("driver")
    }

    "Driver Api will use specified queue when queueActor is set" in {
      val systemApi = new SystemApi()with DriverApi
      val queueActor = system.actorOf(QueueActor.queueActorProps,"DriverApiSpecQueue1")
    }
  }
}
