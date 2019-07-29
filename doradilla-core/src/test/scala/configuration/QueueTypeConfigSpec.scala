package configuration

import akka.actor.ActorSystem
import akka.testkit.TestProbe
import doracore.core.msg.Job.{JobMsg, JobRequest}
import doracore.core.queue.QueueActor
import doracore.core.queue.QueueActor.{FetchTask, RequestListResponse}
import doracore.util.{ConfigService}
import doradilla.conf.DoraConf
import org.scalatest.{FlatSpec, Matchers}

class QueueTypeConfigSpec extends FlatSpec with Matchers{
  "QueueActor with specified type " should
  "use FIFO if type is set to FIFO " in{
    implicit val actorSystem = ActorSystem("DisaptcherTest",DoraConf.config(1503,"back",Some("doradilla.queue.type = Fifo")))
    val proxy = TestProbe()
    ConfigService.getStringOpt(actorSystem.settings.config, "doradilla.queue.type") should be (Some("Fifo"))
    val queueActor= actorSystem.actorOf(QueueActor.queueActorProps)
    val jobRequest = JobRequest(JobMsg("add", "io.github.wherby.doradilla.test"), proxy.ref, proxy.ref,priority = Some(2))
    val jobRequest2 = JobRequest(JobMsg("add2", "io.github.wherby.doradilla.test"), proxy.ref, proxy.ref,priority = Some(10))
    queueActor ! jobRequest
    queueActor ! jobRequest2
    queueActor.tell(FetchTask(1,proxy.ref),proxy.ref)
    proxy.expectMsgPF() {
      case result: RequestListResponse =>
        result.requestList.requests(0) should be (jobRequest)
        println(result)
    }
  }

}
