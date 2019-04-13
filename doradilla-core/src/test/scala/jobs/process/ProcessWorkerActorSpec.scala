package jobs.process

import akka.actor.Props
import akka.testkit.TestProbe
import doradilla.ActorTestClass
import doradilla.core.msg.Job.JobResult
import doradilla.util.ProcessService.ExecuteResult
import jobs.process.ProcessTranActor.{ProcessRequest, SimpleProcessInit}
import play.api.libs.json.Json
import vars.ConstVarTest

/**
  * For jobs.process in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/7
  */
class ProcessWorkerActorSpec extends ActorTestClass{
  "ProcessWorker " must{
    val probe = TestProbe()
    "Return result when finish process" in {
      val processWorker = system.actorOf(Props(new ProcessWorkerActor()), "ProcessWorkerActorSpecWorker")
      val processRequest = SimpleProcessInit( ConstVarTest.processRequest,probe.ref)
      processWorker ! processRequest
      probe.expectMsgPF(){
        case jobResult: JobResult=>println(jobResult)
          val result = Json.parse( jobResult.result).asOpt[ExecuteResult]
          result shouldBe a [Some[_]]
          result.get.exitValue shouldBe (0)
      }
    }
  }
}
