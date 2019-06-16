package doradilla.tool.job.process

import akka.testkit.TestProbe
import doradilla.ActorTestClass
import doradilla.core.msg.Job.JobResult
import doradilla.tool.job.process.ProcessTranActor.SimpleProcessInit
import doradilla.util.CNaming
import vars.ConstVarTest

/**
  * For doradilla.tool.job.process in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/23
  */
class ProcessWorkerActorSpec extends ActorTestClass{
  "Process worker" must{
    "return result when finish command" in {
      val probe = TestProbe()
      val processWorkerActor = system.actorOf(ProcessWorkerActor.processTranActorProps,CNaming.timebasedName( "ProcessWorkerActorSpecWorker1"))
      val simpleProcessInit = SimpleProcessInit(ConstVarTest.processCallMsgTest,probe.ref)
      processWorkerActor ! simpleProcessInit
      Thread.sleep(2000)
      probe.expectMsgPF(){
        case jobResult: JobResult => println(jobResult)
      }
    }
  }
}
