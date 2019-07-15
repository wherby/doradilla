package doracore.core.driver

import akka.actor.{ActorRef, Props}
import akka.testkit.TestProbe
import doracore.ActorTestClass
import doracore.core.driver.DriverActor.ProxyActorMsg
import doracore.core.fsm.FsmActor
import doracore.core.fsm.FsmActor.RegistToDriver
import doracore.core.msg.Job.{JobMsg, JobRequest}
import doracore.util.CNaming
import jobs.fib.FibnacciTranActor.FibRequest
import play.api.libs.json.Json


/**
  * For doradilla.driver in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/30
  */
class DriverActorSpec extends  ActorTestClass  {
  "DriverActor " must{

    "Receive a request message will return a ProxyActorMsg with proxy actor ref" in {
      val proxy = TestProbe()
      val driverActor = system.actorOf(DriverActor.driverActorProps(), CNaming.timebasedName( "driverActor"))
      val requestItem = JobRequest(JobMsg("fibreq", Json.toJson(FibRequest(10)).toString),proxy.ref,proxy.ref)
      driverActor.tell(requestItem,proxy.ref)
      proxy.expectMsgPF(){
        case msg:ProxyActorMsg => println(msg)
           msg
      }
    }

    "DriverActorWithoutFSM attached with FsmActor receive a request message will return a ProxyActorMsg with proxy actor ref" in {
      val proxy = TestProbe()
      val driverActorWithoutFSM = system.actorOf(DriverActor.driverActorPropsWithoutFSM(), "driverActorWithoutFSM")
      val fsmActor = system.actorOf(FsmActor.fsmActorProps, "driverActorFsmActorToUse")
      driverActorWithoutFSM.tell(RegistToDriver(fsmActor),fsmActor)
      val requestItem = JobRequest(JobMsg("fibreq", Json.toJson(FibRequest(10)).toString),proxy.ref,proxy.ref)
      driverActorWithoutFSM.tell(requestItem,proxy.ref)
      proxy.expectMsgPF(){
        case msg:ProxyActorMsg => println(msg)
          msg
      }
      proxy.expectMsgPF(){
        case msg => println(msg)
          msg
      }
    }
  }
}
