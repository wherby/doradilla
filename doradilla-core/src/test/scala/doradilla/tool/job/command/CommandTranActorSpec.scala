package doradilla.tool.job.command

import akka.actor.Props
import akka.testkit.TestProbe
import doradilla.ActorTestClass
import doradilla.core.driver.DriverActor
import doradilla.core.msg.Job.{JobRequest, JobResult}
import vars.ConstVarTest

/**
  * For doradilla.tool.job.command in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/13
  */
class CommandTranActorSpec extends ActorTestClass{
  "Command Tran Actor" must{
    val probe = TestProbe()
    val commandTran = system.actorOf(CommandTranActor.commandTranProps,"CommandTranActorSpecTran")
    val driver = system.actorOf(DriverActor.driverActorProps(),"CommandTranActorDriver")
    "Schedule a processs and return the process result to proxy" in {
      val commandRequest = JobRequest(ConstVarTest.commandJob, probe.ref, commandTran)
      driver ! commandRequest
      probe.expectMsgPF() {
        case msg: JobResult => println(msg)
      }
    }
  }
}
