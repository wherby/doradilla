package doradilla.tool.control

import akka.actor.Props
import akka.testkit.TestProbe
import doradilla.ActorTestClass
import doradilla.base.query.QueryTrait.NotHandleMessage
import doradilla.core.driver.DriverActor
import doradilla.tool.control.ControlActor.ControlMsg
import vars.ConstVarTest

/**
  * For doradilla.tool.control in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/8
  */
class ControlActorSpec  extends  ActorTestClass  {
  "ControlActor" must{
    "send a control msg to a existed actor" in {
      val driverActor = system.actorOf(DriverActor.driverActorProps(), "ControlActorSpecDriver")
      val controlActor = system.actorOf(ControlActor.controlActorProps, "ControlActorSpecControl")
      val probe = TestProbe()
      val controlMsg = ControlMsg(driverActor.path.toString,"TEST")
      controlActor.tell(controlMsg,probe.ref)
      probe.expectMsgPF(){
        case msg:NotHandleMessage => println(msg)
      }
    }
    "Send a control msg to not Existed actor " in {
      val controlActor = system.actorOf(ControlActor.controlActorProps, "ControlActorSpecControl2")
      val probe = TestProbe()
      val controlMsg = ControlMsg("../Notexisted","TEST")
      controlActor.tell(controlMsg,probe.ref)
      probe.expectNoMessage(ConstVarTest.timeout100m)
    }
  }
}
