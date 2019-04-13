package doradilla.tool.job.command

import akka.actor.Props
import akka.testkit.TestProbe
import doradilla.ActorTestClass
import doradilla.core.msg.Job.JobResult
import doradilla.tool.job.command.CommandTranActor.SimpleCommandInit
import doradilla.util.ProcessService.ExecuteResult
import play.api.libs.json.Json
import vars.ConstVarTest

/**
  * For doradilla.tool.job.command in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/13
  */
class CommandWorkerActorSpec extends ActorTestClass {
  "Command Worker" must {
    val probe = TestProbe()
    "Return result when finish command " in {
      val commandWorkerActor = system.actorOf(Props(new CommandWorkerActor()), "CommandWorkerActorSpecWoker")
      val commandRequest = SimpleCommandInit(ConstVarTest.commandRequest, probe.ref)
      println(commandRequest)
      commandWorkerActor ! commandRequest
      probe.expectMsgPF() {
        case jobResult: JobResult => println(jobResult)
          val result = Json.parse(jobResult.result).asOpt[ExecuteResult]
          result shouldBe a[Some[_]]
          result.get.exitValue should (be (0) or be(127))
      }
    }
  }
}
