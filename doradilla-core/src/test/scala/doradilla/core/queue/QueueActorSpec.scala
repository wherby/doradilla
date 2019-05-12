package doradilla.core.queue

import akka.testkit.TestProbe
import doradilla.ActorTestClass
import doradilla.core.msg.Job.{JobMsg, JobRequest}
import doradilla.core.queue.QueueActor.{FetchTask, RequestListResponse}


/**
  * For doradilla.queue in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/24
  */
class QueueActorSpec extends  ActorTestClass  {

  "QueueActor " must {
    "receive RequestItem and fetch number of task" in {
      val proxy = TestProbe()
      val queueActor = system.actorOf(QueueActor.queueActorProps, "queueActor")
      queueActor ! JobRequest(JobMsg("add", "test",None),proxy.ref,proxy.ref)
      proxy.send(queueActor,FetchTask(2,proxy.ref))
      proxy.expectMsgPF(){
        case requestListResponse: RequestListResponse  => requestListResponse.requestList.requests.length should be (1)
          requestListResponse.requestList.requests.head.taskMsg.operation should be ("add")
          requestListResponse.requestList.requests.head.replyTo should be(proxy.ref)
      }
      proxy.send(queueActor,FetchTask(2,proxy.ref))
      proxy.expectMsgPF(){
        case RequestListResponse(reqs,actorRef) => reqs.requests.length should be (0)
      }
    }
  }
}
