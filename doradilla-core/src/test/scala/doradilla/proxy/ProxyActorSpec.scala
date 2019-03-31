package doradilla.proxy

import vars.ConstVar
import akka.actor.Props
import akka.testkit.TestProbe
import doradilla.ActorTestClass
import doradilla.msg.TaskMsg.{EndRequest, RequestMsg, TaskMsg, TaskStatus}
import doradilla.proxy.ProxyActor.{ProxyTaskResult, QueryProxy}
import jobs.fib.FibnacciTranActor.FibRequest
import play.api.libs.json.Json

/**
  * For doradilla.proxy in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/30
  */
class ProxyActorSpec extends  ActorTestClass  {
  "ProxyActor " must{
    val proxy = TestProbe()
    val proxyActor = system.actorOf(Props(new ProxyActor(proxy.ref)), "proxyActor")
    "Receive a requestMsg will put the message to queue" in{
      val requestMsg = RequestMsg(ConstVar.fibTask,proxy.ref,proxy.ref)
      proxyActor ! requestMsg
      proxy.expectMsgPF(){
        case requestMsg: RequestMsg => requestMsg.replyTo should be(proxyActor)
      }
      proxyActor.tell(QueryProxy(),proxy.ref)
      proxy.expectMsgPF(){
        case proxyTaskResult: ProxyTaskResult => proxyTaskResult.status should be(TaskStatus.Queued)
      }
    }
    "ProxyActor will send EndRequest messsage when received Finish message" in{
      proxyActor.tell(TaskStatus.Scheduled,proxy.ref)
      proxyActor! TaskStatus.Finished
      proxy.expectMsgPF(){
        case endRequest: EndRequest =>println(endRequest)
      }
    }

    "ProxyActor will send EndRequest message when received Failed Message" in{
      proxyActor.tell(TaskStatus.Scheduled,proxy.ref)
      proxyActor ! TaskStatus.Failed
      proxy.expectMsgPF(){
        case endRequest: EndRequest =>println(endRequest)
      }
    }

    "ProxyActor will send EndRequest message when received TimeOut Message" in{
      proxyActor.tell(TaskStatus.Scheduled,proxy.ref)
      proxyActor ! TaskStatus.TimeOut
      proxy.expectMsgPF(){
        case endRequest: EndRequest =>println(endRequest)
      }
    }
  }
}
