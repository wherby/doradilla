package jobs.process

import akka.testkit.TestProbe
import doradilla.ActorTestClass
import doradilla.core.driver.DriverActor
import doradilla.core.msg.Job.JobRequest
import vars.ConstVarTest

/**
  * For jobs.process in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/7
  */
class ProcessTranActorSpec extends ActorTestClass {

  "Process TransActor" must {
    val probe = TestProbe()
    val processTran = system.actorOf(ProcessTranActor.processTranActorProps, "ProcessTranActorSpecTran")
    val driver = system.actorOf(DriverActor.driverActorProps(), "ProcessTranActorSpecDiver")
    "Schedule a process and return the process result to proxy" in {
      val processItem = JobRequest(ConstVarTest.processJob, probe.ref, processTran)
      driver ! processItem
      probe.expectMsgPF() {
        case msg => println(msg)
      }
    }
  }
}
