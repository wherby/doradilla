package doracore.util

import akka.actor.{ActorRef, Props}
import akka.testkit.TestProbe
import doracore.ActorTestClass
import doracore.base.{BaseActor}
import doracore.core.msg.Job.WorkerInfo

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



  "DeployService" must{

    "Deploy a correct workerInfo without parameter should return actorRef " in{
      val proxy = TestProbe()
      val testActor = system.actorOf(DeployTestActor.deployTestActorProps)
      testActor.tell(WorkerInfo("doradilla.core.queue.QueueActor",None,None),proxy.ref)
      proxy.expectMsgPF(){
        case res => res shouldBe a [Some[_]]
      }
    }
    "Deploy a correct workerInfo with  parameter should return actorRef " in{
      val proxy = TestProbe()
      val testActor = system.actorOf(DeployTestActor.deployTestActorProps)
      testActor.tell(WorkerInfo("doradilla.util.TestActor2",Some("test2"),None),proxy.ref)
      val ref = proxy.expectMsgPF(){
        case Some(res:ActorRef) =>
          res
      }
      val proxy2 = TestProbe()
      ref.tell("io.github.wherby.doradilla.test",proxy2.ref)
      proxy2.expectMsgPF(){
        case res => res shouldBe ("test2")
      }
    }
  }
}


class DeployTestActor extends  BaseActor{
  override def receive = {
    case workerInfo: WorkerInfo ⇒
      sender()! DeployService.tryToInstanceDeployActor(workerInfo,context)
  }
}

object DeployTestActor{
  val deployTestActorProps = Props(new DeployTestActor())
}
