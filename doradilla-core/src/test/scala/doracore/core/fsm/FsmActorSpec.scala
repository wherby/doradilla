package doracore.core.fsm

import akka.testkit.TestProbe
import doracore.ActorTestClass
import doracore.core.fsm.FsmActor._
import doracore.core.msg.Job.{JobEnd, JobMeta, JobMsg, JobRequest}
import doracore.core.msg.JobControlMsg.ResetFsm

/**
  * For doradilla.fsm in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/24
  */
class FsmActorSpec  extends  ActorTestClass {
  "FsmActor " must{
    "can be query state and if Finish the task will return to Idle status" in{
      val proxy = TestProbe()
      val proxy2 = TestProbe()
      val fsmActor = system.actorOf(FsmActor.fsmActorProps, "fsmtest")
      fsmActor ! SetDriver(proxy.ref)
      proxy.send(fsmActor,QueryState())
      proxy.expectMsgPF(){
        case res =>
          res should be(Uninitialized)
      }
      val requestMsg = JobRequest(JobMsg("add", "io.github.wherby.doradilla.test"),proxy2.ref,proxy.ref)
      proxy.send(fsmActor, ResetFsm())
      proxy.send(fsmActor, "Unhandled msg")
      proxy.send(fsmActor,requestMsg)
      proxy.expectMsgPF(){
        case res =>
          println(res)
          res shouldBe a [JobRequest]
      }
      proxy.send(fsmActor,QueryState())
      proxy.expectMsgPF(){
        case res =>
          println(res)
          res shouldBe a [Task]
      }

      fsmActor ! JobEnd(requestMsg)
      proxy.expectMsgPF(){
        case res:FetchJob => println(res)
      }
      proxy.send(fsmActor,QueryState())
      proxy.expectMsgPF(){
        case res =>
          println(res)
          res shouldBe (Uninitialized)
      }
    }


    "Will not handle wrong JobResult message " in{
      val proxy = TestProbe()
      val proxy2 = TestProbe()
      val proxy3 = TestProbe()
      val fsmActor = system.actorOf(FsmActor.fsmActorProps, "fsmtest2")
      fsmActor ! SetDriver(proxy.ref)
      proxy.send(fsmActor,QueryState())
      proxy.expectMsgPF(){
        case res =>
          res should be(Uninitialized)
      }
      val requestMsg = JobRequest(JobMsg("add", "io.github.wherby.doradilla.test"),proxy2.ref,proxy.ref)
      proxy.send(fsmActor, ResetFsm())
      proxy.send(fsmActor, "Unhandled msg")
      proxy.send(fsmActor,requestMsg)
      proxy.expectMsgPF(){
        case res =>
          println(res)
          res shouldBe a [JobRequest]
      }
      proxy.send(fsmActor,QueryState())
      proxy.expectMsgPF(){
        case res =>
          println(res)
          res shouldBe a [Task]
      }
      proxy3.send(fsmActor,QueryState())
      proxy3.expectMsgPF(){
        case res =>
          println(res)
          res shouldBe a [Task]
      }
      val requestMsg3 = JobRequest(JobMsg("BBB", "io.github.wherby.doradilla.test"),proxy2.ref,proxy.ref,jobMetaOpt = Some(JobMeta("2222")))
      fsmActor ! JobEnd(requestMsg3)
      proxy3.send(fsmActor,QueryState())
      proxy3.expectMsgPF(){
        case res =>
          println(res)
          res shouldBe a [Task]
      }
      fsmActor ! JobEnd(requestMsg)
      proxy.expectMsgPF(){
        case res:FetchJob => println(res)
      }
      proxy.send(fsmActor,QueryState())
      proxy.expectMsgPF(){
        case res =>
          println(res)
          res shouldBe (Uninitialized)
      }
    }


    "Will receive Fetch message when idle timeout " in {
      val proxy2 = TestProbe()
      val fsmActor2 = system.actorOf(FsmActor.fsmActorProps, "fsmtest4")
      fsmActor2 ! SetDriver(proxy2.ref)
      proxy2.expectMsgPF(){
        case res: FetchJob => println(res)
          res
      }
    }
  }
}
