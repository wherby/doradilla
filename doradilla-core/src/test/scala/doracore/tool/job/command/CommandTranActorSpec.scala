package doracore.tool.job.command

import akka.testkit.TestProbe
import doracore.ActorTestClass
import doracore.core.driver.DriverActor
import doracore.core.msg.Job.{JobRequest, JobResult}
import doracore.core.msg.TranslationMsg.{TranslationDataError, TranslationOperationError}
import doracore.util.CNaming
import vars.ConstVarTest

/**
  * For doradilla.tool.job.command in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/13
  */
class CommandTranActorSpec extends ActorTestClass{
  "Command Tran Actor" must{
    val probe = TestProbe()
    val commandTran = system.actorOf(CommandTranActor.commandTranProps,CNaming.timebasedName( "CommandTranActorSpecTran"))
    val driver = system.actorOf(DriverActor.driverActorProps(), CNaming.timebasedName( "CommandTranActorDriver"))
    "Schedule a command and return the command result to proxy" in {
      val commandRequest = JobRequest(ConstVarTest.commandJob, probe.ref, commandTran)
      driver ! commandRequest
      probe.expectMsgPF() {
        case msg: JobResult => println(msg)
      }
    }

    "Return Operation error when operation is not included " in {
      val commandRequest = JobRequest(ConstVarTest.commandJob.copy(operation = "OperationNotInclude"), probe.ref, commandTran)
      commandTran.tell( commandRequest, probe.ref)
      probe.expectMsgPF() {
        case msg: TranslationOperationError => println(msg)
      }
    }

    "Return data error when data is not in correct format " in {
      val commandRequest = JobRequest(ConstVarTest.commandJob.copy(data =1), probe.ref, commandTran)
      commandTran.tell( commandRequest, probe.ref)
      probe.expectMsgPF() {
        case msg: TranslationDataError => println(msg)
      }
    }
  }
}
