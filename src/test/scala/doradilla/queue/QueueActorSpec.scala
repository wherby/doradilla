package doradilla.queue

import akka.actor.{ Props}
import akka.testkit.{ TestProbe}
import doradilla.{ActorTestClass}
import doradilla.msg.TaskMsg.{RequestItem, RequestMsg}
import doradilla.queue.QueueActor.{FetchTask, RequestList}


/**
  * For doradilla.queue in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/24
  */
class QueueActorSpec extends  ActorTestClass  {

  "QueueActor " must {
    "receive RequestItem and fetch number of task" in {
      val proxy = TestProbe()
      val queueActor = system.actorOf(Props(new QueueActor), "queueActor")
      queueActor ! RequestItem(RequestMsg("add", "test",None),proxy.ref)
      proxy.send(queueActor,FetchTask(2))
      proxy.expectMsgPF(){
        case RequestList(reqs) => reqs.length should be (1)
          reqs.head.requestMsg.operation should be ("add")
          reqs.head.actorRef should be(proxy.ref)
      }
      proxy.send(queueActor,FetchTask(2))
      proxy.expectMsgPF(){
        case RequestList(reqs) => reqs.length should be (0)
      }
    }
  }
}
