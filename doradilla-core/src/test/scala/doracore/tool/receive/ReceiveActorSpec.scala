package doracore.tool.receive

import akka.testkit.TestProbe
import doracore.ActorTestClass
import doracore.core.driver.DriverActor.ProxyActorMsg
import doracore.core.msg.Job.{JobResult, JobStatus}
import doracore.tool.receive.ReceiveActor.{FetchResult, ProxyControlMsg, QueryResult}

/**
  * For doradilla.tool.receive in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/13
  */
class ReceiveActorSpec extends ActorTestClass{
  "Receive Actor " must{
    "Send io.github.wherby.doradilla.back resut when jobResult receive later" in{
      val receiveActor = system.actorOf(ReceiveActor.receiveActorProps)
      val probe = TestProbe()
      probe.send(receiveActor,FetchResult())
      receiveActor ! JobResult(JobStatus.Finished,"finished")
      probe.expectMsgPF(){
        case jobResult: JobResult=> jobResult.result should be("finished")
      }
    }

    "Send io.github.wherby.doradilla.back resut when jobResult receive earlier" in{
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
      receiveActor ! ProxyControlMsg("io.github.wherby.doradilla.test")
      probe.expectMsgPF(){
        case msg=> msg should be("io.github.wherby.doradilla.test")
      }
    }

    "Send QueryResult msg to  proxy" in{
      val receiveActor = system.actorOf(ReceiveActor.receiveActorProps)
      val probe = TestProbe()
      receiveActor ! ProxyActorMsg(probe.ref)
      receiveActor.tell(QueryResult(),probe.ref)
      probe.expectMsgPF(){
        case msg=> msg should be (None)
      }
      receiveActor ! JobResult(JobStatus.Finished,"finished")
      receiveActor.tell(QueryResult(),probe.ref)
      probe.expectMsgPF(){
        case msg=> msg should be (Some(JobResult(JobStatus.Finished,"finished")))
      }
    }
  }
}
