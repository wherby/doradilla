package doracore.base

import akka.actor.Props
import akka.testkit.TestProbe
import doracore.ActorTestClass
import doracore.base.query.QueryTrait.{ChildInfo, NotHandleMessage, QueryChild}
import doracore.util.CNaming


/**
  * For doradilla.base in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/23
  */
class BaseActorSpec extends  ActorTestClass  {

  "An actor extends queryActor " must{
    "return child info response" in{
      val proxy = TestProbe()
      val name =CNaming.timebasedName( "testActor")
      val testActor = system.actorOf(TestActor.testActorProps,name )
      testActor ! QueryChild(proxy.ref)
      proxy.expectMsgPF(){
        case ChildInfo(root,child,_)=> root should endWith  (s"/user/$name")
      }
      proxy.send(testActor,"io.github.wherby.doradilla.test")
      proxy.expectMsgPF(){
        case notHandleMessage: NotHandleMessage =>println(notHandleMessage)
      }
    }

    "handle exception" in{
      val proxy = TestProbe()
      val testActor = system.actorOf(TestActor.testActorProps, CNaming.timebasedName( "testActor"))
      testActor !"Crash"
      testActor ! NotHandleMessage("dssd")
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
    case "Crash" =>
      println("about to crash")
      throw new Exception("failed")
  }
}

object TestActor{
  val testActorProps = Props(new TestActor())
}