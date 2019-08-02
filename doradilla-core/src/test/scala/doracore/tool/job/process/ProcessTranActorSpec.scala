package doracore.tool.job.process

import akka.testkit.TestProbe
import doracore.ActorTestClass
import doracore.core.driver.DriverActor
import doracore.core.msg.Job.{JobRequest, JobResult}
import doracore.core.msg.TranslationMsg.{TranslationDataError, TranslationOperationError}
import doracore.util.CNaming
import vars.ConstVarTest

/**
  * For doradilla.tool.job.process in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/23
  */
class ProcessTranActorSpec extends ActorTestClass{
  "Process Tran Actor" must{
    "Schedule a prcess and return the command result to proxy" in {
      val probe = TestProbe()
      val processTran = system.actorOf(ProcessTranActor.processTranActorProps, CNaming.timebasedName( "ProcessTranActorSpecTran1"))
      val driver = system.actorOf(DriverActor.driverActorProps(),CNaming.timebasedName( "ProcessTranActorSpecDriver1"))
      val processRequest = JobRequest(ConstVarTest.processJob,probe.ref,processTran)
      driver! processRequest
      probe.expectMsgPF() {
        case msg:JobResult => println(msg)
      }
    }

    "Schedule a SimpleProcessFuture and return the command result to proxy" in {
      val probe = TestProbe()
      val processTran = system.actorOf(ProcessTranActor.processTranActorProps, CNaming.timebasedName( "ProcessTranActorSpecTran1"))
      val driver = system.actorOf(DriverActor.driverActorProps(),CNaming.timebasedName( "ProcessTranActorSpecDriver1"))
      val processRequest = JobRequest(ConstVarTest.processJob.copy(operation = "SimpleProcessFuture"),probe.ref,processTran)
      driver! processRequest
      probe.expectMsgPF() {
        case msg:JobResult => println(msg)
      }
    }

    "throw exception when process type is not include" in {
      val probe = TestProbe()
      val processTran = system.actorOf(ProcessTranActor.processTranActorProps, CNaming.timebasedName( "ProcessTranActorSpecTran1"))
      val processRequest = JobRequest(ConstVarTest.processJob.copy(operation = "operationNotExisted"),probe.ref,processTran)
      processTran.tell( processRequest,probe.ref)
      probe.expectMsgPF() {
        case msg:TranslationOperationError => println(msg)
      }
    }

    "throw exception when process data is not in ProcessCallMsg" in {
      val probe = TestProbe()
      val processTran = system.actorOf(ProcessTranActor.processTranActorProps, CNaming.timebasedName( "ProcessTranActorSpecTran1"))
      val processRequest = JobRequest(ConstVarTest.processJob.copy(data = "data not in format"),probe.ref,processTran)
      processTran.tell( processRequest,probe.ref)
      probe.expectMsgPF() {
        case msg:TranslationDataError => println(msg)
      }
    }
  }
}
