package doradilla.fsm

import akka.actor.Props
import akka.testkit.TestProbe
import doradilla.ActorTestClass
import doradilla.fsm.FsmActor._
import doradilla.msg.Job.{JobEnd, JobRequest, JobMsg}

/**
  * For doradilla.fsm in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/24
  */
class FsmActorSpec  extends  ActorTestClass {
  "FsmActor " must{
    "can be query state and if Finish the task will return to Idle status" in{
      val proxy = TestProbe()
      val proxy2 = TestProbe()
      val fsmActor = system.actorOf(Props(new FsmActor), "fsmtest")
      fsmActor ! SetDriver(proxy.ref)
      proxy.send(fsmActor,QueryState())
      proxy.expectMsgPF(){
        case res =>
          res should be(Uninitialized)
      }
      val requestMsg = JobRequest(JobMsg("add", "test",None),proxy2.ref,proxy.ref)
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
    "Will receive Fetch message when idle timeout " in {
      val proxy2 = TestProbe()
      val fsmActor2 = system.actorOf(Props(new FsmActor), "fsmtest2")
      fsmActor2 ! SetDriver(proxy2.ref)
      proxy2.expectMsgPF(){
        case res: FetchJob => println(res)
          res
      }
    }
  }
}
