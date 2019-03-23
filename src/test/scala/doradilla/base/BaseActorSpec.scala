package doradilla.base

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import doradilla.base.BaseActor.NotHandleMessage
import doradilla.base.query.QueryActor.{ChildInfo, QueryChild}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}


/**
  * For doradilla.base in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/23
  */
class BaseActorSpec extends  TestKit(ActorSystem("MySpec")) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll {
  class TestActor extends  BaseActor{
     override def receive = {
      case "init" ⇒
       Some("Up and running")
    }
  }

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "An actor extends queryActor " must{
    "return child info response" in{
      val proxy = TestProbe()
      val testActor = system.actorOf(Props(new TestActor),"test")
      testActor ! QueryChild(proxy.ref)
      proxy.expectMsgPF(){
        case ChildInfo(root,child,_)=> root should be ("akka://MySpec/user/test")
      }
      proxy.send(testActor,"test")
      proxy.expectMsg(NotHandleMessage("test"))
    }
  }
}