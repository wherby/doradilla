package doradilla.util

import akka.actor.{ActorRef, Props}
import akka.testkit.TestProbe
import doradilla.ActorTestClass
import doradilla.base.BaseActor
import doradilla.msg.TaskMsg.WorkerInfo

/**
  * For doradilla.util in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/30
  */
class TestActor2(config:String) extends  BaseActor{
  override def receive = {
    case msg => sender() ! config
  }
}

class DeployServiceSpec extends  ActorTestClass  {
  class TestActor extends  BaseActor{
    override def receive = {
      case workerInfo: WorkerInfo ⇒
        sender()! DeployService.tryToInstanceDeployActor(workerInfo,context)
    }
  }


  "DeployService" must{
    val proxy = TestProbe()
    val testActor = system.actorOf(Props(new TestActor()))
    "Deploy a correct workerInfo without parameter should return actorRef " in{
      testActor.tell(WorkerInfo("doradilla.queue.QueueActor",None),proxy.ref)
      proxy.expectMsgPF(){
        case res => res shouldBe a [Some[_]]
      }
    }
    "Deploy a correct workerInfo with  parameter should return actorRef " in{
      testActor.tell(WorkerInfo("doradilla.util.TestActor2",Some("test2")),proxy.ref)
      val ref = proxy.expectMsgPF(){
        case res:Some[ActorRef] =>
          res
      }
      val proxy2 = TestProbe()
      ref.get.tell("test",proxy2.ref)
      proxy2.expectMsgPF(){
        case res => res shouldBe ("test2")
      }
    }
  }
}
