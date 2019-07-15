package doracore.tool.job.command

import akka.testkit.TestProbe
import doracore.ActorTestClass
import doracore.core.msg.Job.{JobResult, JobStatus}
import doracore.tool.job.command.CommandTranActor.SimpleCommandInit
import vars.ConstVarTest

/**
  * For doradilla.tool.job.command in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/13
  */
class CommandWorkerActorSpec extends ActorTestClass {
  "Command Worker" must {
    val probe = TestProbe()
    "Return result when finish command " in {
      val commandWorkerActor = system.actorOf(CommandWorkerActor.commandWorkerProps, "CommandWorkerActorSpecWoker")
      val commandRequest = SimpleCommandInit(ConstVarTest.commandRequest, probe.ref)
      println(commandRequest)
      commandWorkerActor ! commandRequest
      probe.expectMsgPF() {
        case jobResult: JobResult => println(jobResult)
          jobResult.taskStatus should be (JobStatus.Finished)
      }
    }
  }
}
