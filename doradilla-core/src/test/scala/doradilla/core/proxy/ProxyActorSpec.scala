package doradilla.core.proxy

import vars.ConstVarTest
import akka.actor.Props
import akka.testkit.TestProbe
import doradilla.ActorTestClass
import doradilla.core.msg.Job._
import doradilla.core.proxy.ProxyActor.{ProxyTaskResult, QueryProxy}

/**
  * For doradilla.proxy in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/30
  */
class ProxyActorSpec extends  ActorTestClass  {
  "ProxyActor " must{
    val proxy = TestProbe()
    val proxyActor = system.actorOf(Props(new ProxyActor(proxy.ref)), "proxyActor")
    "Receive a requestMsg will put the message to queue" in{
      val requestMsg = JobRequest(ConstVarTest.fibTask,proxy.ref,proxy.ref)
      proxyActor ! requestMsg
      proxy.expectMsgPF(){
        case requestMsg: JobRequest => requestMsg.replyTo should be(proxyActor)
      }
      proxyActor.tell(QueryProxy(),proxy.ref)
      proxy.expectMsgPF(){
        case proxyTaskResult: ProxyTaskResult => proxyTaskResult.status should be(JobStatus.Queued)
      }
    }
    "ProxyActor will send EndRequest messsage when received Finish message" in{
      proxyActor.tell(JobStatus.Scheduled,proxy.ref)
      proxyActor! JobStatus.Finished
      proxy.expectMsgPF(){
        case endRequest: JobEnd =>println(endRequest)
      }
    }

    "ProxyActor will send EndRequest message when received Failed Message" in{
      proxyActor.tell(JobStatus.Scheduled,proxy.ref)
      proxyActor ! JobStatus.Failed
      proxy.expectMsgPF(){
        case endRequest: JobEnd =>println(endRequest)
      }
    }

    "ProxyActor will send EndRequest message when received TimeOut Message" in{
      proxyActor.tell(JobStatus.Scheduled,proxy.ref)
      proxyActor ! JobStatus.TimeOut
      proxy.expectMsgPF(){
        case endRequest: JobEnd =>println(endRequest)
      }
    }
  }
}
