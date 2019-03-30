package doradilla.fsm

import akka.actor.Props
import akka.testkit.TestProbe
import doradilla.ActorTestClass
import doradilla.fsm.FsmActor.{QueryState, Task, Uninitialized}
import doradilla.msg.TaskMsg.{RequestMsg, TaskMsg}

/**
  * For doradilla.fsm in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/24
  */
class FsmActorSpec  extends  ActorTestClass {
  "FsmActor " must{
    "can be query state" in{
      val proxy = TestProbe()
      val fsmActor = system.actorOf(Props(new FsmActor), "fsmtest")
      proxy.send(fsmActor,QueryState())
      proxy.expectMsgPF(){
        case res =>
          res should be(Uninitialized)
      }
      proxy.send(fsmActor, RequestMsg(TaskMsg("add", "test",None),proxy.ref,proxy.ref))
      proxy.expectMsgPF(){
        case res =>
          println(res)
          res shouldBe a [RequestMsg]
      }
      proxy.send(fsmActor,QueryState())
      proxy.expectMsgPF(){
        case res =>
          println(res)
          res shouldBe a [Task]
      }
    }
  }
}
