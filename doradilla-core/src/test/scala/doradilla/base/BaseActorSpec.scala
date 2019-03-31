package doradilla.base

import akka.actor.{ Props}
import akka.testkit.{ TestProbe}
import doradilla.ActorTestClass
import doradilla.base.BaseActor.NotHandleMessage
import doradilla.base.query.QueryActor.{ChildInfo, QueryChild}


/**
  * For doradilla.base in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/23
  */
class BaseActorSpec extends  ActorTestClass  {

  class TestActor extends  BaseActor{
     override def receive = {
      case "init" ⇒
       Some("Up and running")
    }
  }

  "An actor extends queryActor " must{
    "return child info response" in{
      val proxy = TestProbe()
      val testActor = system.actorOf(Props(new TestActor),"test")
      testActor ! QueryChild(proxy.ref)
      proxy.expectMsgPF(){
        case ChildInfo(root,child,_)=> root should be ("akka://AkkaQuickstartSpec/user/test")
      }
      proxy.send(testActor,"test")
      proxy.expectMsg(NotHandleMessage("test"))
    }
  }
}