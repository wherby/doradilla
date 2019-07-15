package scenario

import akka.testkit.TestProbe
import doracore.ActorTestClass
import doracore.core.driver.DriverActor
import doracore.core.msg.Job.JobRequest
import doracore.util.CNaming
import jobs.fib.FibnacciTranActor
import vars.ConstVarTest

/**
  * For scenario in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/31
  */
class PostJobToDriverSpec extends ActorTestClass {
  "Post FibJob to Driver will start Fib task" should  {


    "driver actor will handle RequestMsg " in {
      val fibTran = system.actorOf(FibnacciTranActor.fibnacciTranActorProps,CNaming.timebasedName("fibTran"))
      val driver = system.actorOf(DriverActor.driverActorProps(),CNaming.timebasedName("driverActor"))
      val probe = TestProbe()
      for (i <- 0 to 20) {
        val request = JobRequest(ConstVarTest.fibTaskN(i), probe.ref, fibTran)
        driver ! request
      }
      for (i <- 0 to 20) {
        probe.expectMsgPF() {
          case msg => println(msg)
        }
      }

    }
  }
}
