package doracore.core.queue

import akka.testkit.TestProbe
import doracore.ActorTestClass
import doracore.core.msg.Job.{JobMsg, JobRequest, JobResult, JobStatus}
import doracore.core.queue.QueueActor._
import doracore.util.CNaming


/**
  * For doradilla.queue in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/24
  */
class QueueActorSpec extends ActorTestClass {

  "QueueActor " must {
    "receive RequestItem and fetch number of task" in {
      val proxy = TestProbe()
      val queueActor = system.actorOf(QueueActor.queueActorProps, CNaming.timebasedName("queueActor"))
      queueActor ! JobRequest(JobMsg("add", "io.github.wherby.doradilla.test"), proxy.ref, proxy.ref)
      proxy.send(queueActor, FetchTask(2, proxy.ref))
      proxy.expectMsgPF() {
        case requestListResponse: RequestListResponse => requestListResponse.requestList.requests.length should be(1)
          requestListResponse.requestList.requests.head.taskMsg.operation should be("add")
          requestListResponse.requestList.requests.head.replyTo should be(proxy.ref)
      }
      proxy.send(queueActor, FetchTask(2, proxy.ref))
      proxy.expectMsgPF() {
        case RequestListResponse(reqs, actorRef) => reqs.requests.length should be(0)
      }
    }
  }

  "QueueActor for remove and snap " must {
    "Remove job and send cancel result to replyTo Actor " in {
      val proxy = TestProbe()
      val queueActor = system.actorOf(QueueActor.queueActorProps, CNaming.timebasedName("queueActor"))
      val jobRequest = JobRequest(JobMsg("add", "io.github.wherby.doradilla.test"), proxy.ref, proxy.ref)
      val jobRequest2 = JobRequest(JobMsg("add2", "io.github.wherby.doradilla.test"), proxy.ref, proxy.ref)
      queueActor ! jobRequest
      queueActor ! jobRequest2
      queueActor ! RemoveJob(jobRequest)
      proxy.expectMsgPF() {
        case jobResult: JobResult =>
          jobResult.taskStatus should be(JobStatus.Canceled)
      }
      queueActor.tell(Snap(),proxy.ref)
      proxy.expectMsgPF(){
        case snapResult: SnapResult => snapResult.queueJobs should be (Seq(jobRequest2))
      }
    }
  }
}
