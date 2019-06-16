package doradilla.tool.job.process

import akka.testkit.TestProbe
import doradilla.ActorTestClass
import doradilla.core.driver.DriverActor
import doradilla.core.msg.Job.{JobRequest}
import vars.ConstVarTest

/**
  * For doradilla.tool.job.process in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/23
  */
class ProcessTranActorSpec extends ActorTestClass{
  "Process Tran Actor" must{
    "Schedule a prcess and return the command result to proxy" in {
      val probe = TestProbe()
      val processTran = system.actorOf(ProcessTranActor.processTranActorProps, "ProcessTranActorSpecTran1")
      val driver = system.actorOf(DriverActor.driverActorProps(),"ProcessTranActorSpecDriver1")
      val processRequest = JobRequest(ConstVarTest.processJob,probe.ref,processTran)
      driver! processRequest
      Thread.sleep(1000)
      probe.expectMsgPF() {
        case msg => println(msg)
      }
    }
  }
}
