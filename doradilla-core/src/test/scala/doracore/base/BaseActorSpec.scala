package doracore.base

import akka.actor.Props
import akka.testkit.TestProbe
import doracore.ActorTestClass
import doracore.base.query.QueryTrait.{ChildInfo, NotHandleMessage, QueryChild}


/**
  * For doradilla.base in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/23
  */
class BaseActorSpec extends  ActorTestClass  {

  "An actor extends queryActor " must{
    "return child info response" in{
      val proxy = TestProbe()
      val testActor = system.actorOf(TestActor.testActorProps,"io.github.wherby.doradilla.test")
      testActor ! QueryChild(proxy.ref)
      proxy.expectMsgPF(){
        case ChildInfo(root,child,_)=> root should be ("akka://AkkaQuickstartSpec/user/io.github.wherby.doradilla.test")
      }
      proxy.send(testActor,"io.github.wherby.doradilla.test")
      proxy.expectMsgPF(){
        case notHandleMessage: NotHandleMessage =>println(notHandleMessage)
      }
    }
  }
}

class TestActor extends  BaseActor{
  override def receive = {
    case "init" =>
      Some("Up and running")
  }
}

object TestActor{
  val testActorProps = Props(new TestActor())
}