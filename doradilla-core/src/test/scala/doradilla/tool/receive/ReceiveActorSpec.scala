package doradilla.tool.receive

import akka.actor.{PoisonPill, Props}
import akka.testkit.TestProbe
import doradilla.ActorTestClass
import doradilla.core.driver.DriverActor.ProxyActorMsg
import doradilla.core.msg.Job.{JobRequest, JobResult, JobStatus}
import doradilla.tool.receive.ReceiveActor.{FetchResult, ProxyControlMsg, StopProxy}

/**
  * For doradilla.tool.receive in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/13
  */
class ReceiveActorSpec extends ActorTestClass{
  "Receive Actor " must{
    "Send back resut when jobResult receive later" in{
      val receiveActor = system.actorOf(ReceiveActor.receiveActorProps)
      val probe = TestProbe()
      probe.send(receiveActor,FetchResult())
      receiveActor ! JobResult(JobStatus.Finished,"finished")
      probe.expectMsgPF(){
        case jobResult: JobResult=> jobResult.result should be("finished")
      }
    }

    "Send back resut when jobResult receive earlier" in{
      val receiveActor = system.actorOf(ReceiveActor.receiveActorProps)
      val probe = TestProbe()
      receiveActor ! JobResult(JobStatus.Finished,"finished")
      probe.send(receiveActor,FetchResult())
      probe.expectMsgPF(){
        case jobResult: JobResult=> jobResult.result should be("finished")
      }
    }

    "Send Proxy control msg to  proxy" in{
      val receiveActor = system.actorOf(ReceiveActor.receiveActorProps)
      val probe = TestProbe()
      receiveActor ! ProxyActorMsg(probe.ref)
      receiveActor ! ProxyControlMsg("test")
      probe.expectMsgPF(){
        case msg=> msg should be("test")
      }
    }
  }
}
