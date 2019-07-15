package jobs.fib

import akka.testkit.TestProbe
import doracore.ActorTestClass
import doracore.core.msg.Job.JobResult
import doracore.util.CNaming
import jobs.fib.FibnacciTranActor.{FibAdd, FibInit, FibRequest, FibResult}
import play.api.libs.json.Json

/**
  * For jobs.jobs.jobs.fib in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/30
  */
class FibWorkActorSpec extends ActorTestClass {
  "FibWorkActor receive message " must {
    val proxy = TestProbe()

    "Receive a jobs.jobs.fib task for 10 should return result " in {
      val config = Json.toJson(FibRequest(10)).toString
      val fibWorkerActor = system.actorOf(FibWorkActor.fibWorkActorProps(config), CNaming.timebasedName( "FibnacciWorker"))
      fibWorkerActor.tell(FibInit(FibAdd(1, 1, 0), proxy.ref), proxy.ref)
      proxy.expectMsgPF() {
        case taskResult: JobResult => val fibResult = Json.parse(taskResult.result.toString).as[FibResult]
          fibResult.fa should be(55)
          fibResult.a should be(10)
      }
    }
  }
}
