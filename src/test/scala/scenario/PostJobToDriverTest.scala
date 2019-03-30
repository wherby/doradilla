package scenario

import vars.ConstVar
import akka.actor.{ActorRef, Props}
import akka.testkit.TestProbe
import doradilla.ActorTestClass
import doradilla.driver.DriverActor
import doradilla.msg.TaskMsg.RequestMsg
import doradilla.proxy.ProxyActor.QueryProxy
import jobs.fib.FibnacciTranActor

/**
  * For scenario in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/31
  */
class PostJobToDriverTest extends  ActorTestClass  {
  "Post FibJob to Driver will start Fib task" must{
    val fibTran = system.actorOf(Props(new FibnacciTranActor))
    val driver = system.actorOf(Props(new DriverActor()))
    val probe = TestProbe()
    val request = RequestMsg(ConstVar.fibTask,probe.ref,fibTran)
    "driver actor will handle RequestNsg " in{
        driver.tell(request,probe.ref)
       probe.expectMsgPF(){
        case proxy:ActorRef =>
          proxy
      }
      probe.expectMsgPF(){
        case msg=> println(msg)
      }
      driver.tell(request,probe.ref)
      probe.expectMsgPF(){
        case proxy:ActorRef =>
          proxy
      }
      probe.expectMsgPF(){
        case msg=> println(msg)
      }
      driver.tell(request,probe.ref)
      probe.expectMsgPF(){
        case proxy:ActorRef =>
          proxy
      }
      probe.expectMsgPF(){
        case msg=> println(msg)
      }
    }
  }
}
