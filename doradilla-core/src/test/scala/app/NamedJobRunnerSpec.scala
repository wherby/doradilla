package app

import doracore.ActorTestClass
import doracore.core.msg.Job.JobMsg
import doracore.util.{ProcessService, ProcessServiceSpec}
import doracore.vars.ConstVars
import doradilla.back.BackendServer
import doradilla.conf.TestVars
import org.scalatest.Matchers

import scala.concurrent.Await

/**
  * For app in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/12/14
  */
class NamedJobRunnerSpec  extends ActorTestClass with Matchers {
  ProcessService.nameToClassOpt = ProcessServiceSpec.safeProcessServiceNameToClassOpt
  import scala.concurrent.ExecutionContext.Implicits.global
  val timeout = ConstVars.timeout1S *4
  "Named Job Runner" should{
    "start new driver when name is different" in{
      val backendServer = BackendServer.startup(Some(1600))
      val job1 = TestVars.sleepProcessJob

      BackendServer.runNamedProcessCommand(job1, "job1")
      val job2 = TestVars.processJob
      val resultFuture = BackendServer.runNamedProcessCommand(job2,"job2")
      val result = Await.ready(resultFuture,timeout)
      println(result)
    }

    "use same driver when name same" in{
      val backendServer = BackendServer.startup(Some(1600))
      val job1 = TestVars.sleepProcessJob

      val result1Future = BackendServer.runNamedProcessCommand(job1, "job3")
      val job2 = TestVars.processJob
      val resultFuture = BackendServer.runNamedProcessCommand(job2,"job3")
      var timeOut = false

      try{
        val result = Await.ready(resultFuture,timeout)
        println(result)
      }catch {
        case exception: Exception=>
          timeOut = true
          println(exception)
      }
      timeOut shouldBe(true)
    }
  }
}
