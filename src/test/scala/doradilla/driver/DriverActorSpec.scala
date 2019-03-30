package doradilla.driver

import akka.actor.{ActorRef, Props}
import akka.testkit.TestProbe
import doradilla.ActorTestClass
import doradilla.msg.TaskMsg.{RequestMsg, TaskMsg}
import jobs.fib.FibnacciTranActor.FibRequest
import play.api.libs.json.Json


/**
  * For doradilla.driver in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/30
  */
class DriverActorSpec extends  ActorTestClass  {
  "DriverActor " must{
    val proxy = TestProbe()
    val driverActor = system.actorOf(Props(new DriverActor), "driverActor")
    val requestItem = RequestMsg(TaskMsg("fibreq", Json.toJson(FibRequest(10)).toString),proxy.ref,proxy.ref)
    "Receive a request message will return a proxy actor ref" in {
      driverActor.tell(requestItem,proxy.ref)
      proxy.expectMsgPF(){
        case msg:ActorRef => println(msg)
           msg
      }
    }
  }
}
