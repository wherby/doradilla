package doracore.tool.job.process

import akka.testkit.TestProbe
import doracore.ActorTestClass
import doracore.core.msg.Job.{JobResult, JobStatus}
import doracore.tool.job.process.ProcessTranActor.SimpleProcessInit
import doracore.util.CNaming
import doracore.util.ProcessService.ProcessResult
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

    "return fail result when process Can't be called" in {
      val probe = TestProbe()
      val processWorkerActor = system.actorOf(ProcessWorkerActor.processTranActorProps,CNaming.timebasedName( "ProcessWorkerActorSpecWorker1"))
      val simpleProcessInit = SimpleProcessInit(ConstVarTest.processCallMsgTest.copy(clazzName = "ClassNotExisted"),probe.ref)
      processWorkerActor ! simpleProcessInit
      Thread.sleep(2000)
      probe.expectMsgPF(){
        case jobResult: JobResult => println(jobResult)
          (jobResult.result.asInstanceOf[ProcessResult]).jobStatus should be (JobStatus.Failed)
      }
    }

    "return fail result when method Can't be called" in {
      val probe = TestProbe()
      val processWorkerActor = system.actorOf(ProcessWorkerActor.processTranActorProps,CNaming.timebasedName( "ProcessWorkerActorSpecWorker1"))
      val simpleProcessInit = SimpleProcessInit(ConstVarTest.processCallMsgTest.copy(methodName = "methodNotExied"),probe.ref)
      processWorkerActor ! simpleProcessInit
      Thread.sleep(2000)
      probe.expectMsgPF(){
        case jobResult: JobResult => println(jobResult)
          (jobResult.result.asInstanceOf[ProcessResult]).jobStatus should be (JobStatus.Failed)
      }
    }
  }
}
