package doradilla.tool.job.process

import akka.testkit.TestProbe
import doradilla.ActorTestClass
import doradilla.core.msg.Job.JobResult
import doradilla.tool.job.process.ProcessTranActor.SimpleProcessInit
import vars.ConstVarTest

/**
  * For doradilla.tool.job.process in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/23
  */
class ProcessWorkerActorSpec extends ActorTestClass{
  "Process worker" must{
    val probe = TestProbe()
    "return result when finish command" in {
      val processWorkerActor = system.actorOf(ProcessWorkerActor.processTranActorProps,"ProcessWorkerActorSpecWorker1")
      val simpleProcessInit = SimpleProcessInit(ConstVarTest.processCallMsgTest,probe.ref)
      processWorkerActor ! simpleProcessInit
      Thread.sleep(1000)
      probe.expectMsgPF(){
        case jobResult: JobResult => println(jobResult)
      }
    }
  }
}
