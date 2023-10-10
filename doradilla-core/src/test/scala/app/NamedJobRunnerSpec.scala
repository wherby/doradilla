package app

import doracore.ActorTestClass
import doracore.core.msg.Job.{JobMeta, JobMsg}
import doracore.util.{ProcessService, ProcessServiceSpec}
import doracore.vars.ConstVars
import doradilla.back.BackendServer
import doradilla.conf.TestVars
import org.scalatest.Matchers

import scala.concurrent.{Await, Future}

/**
  * For app in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/12/14
  */
class NamedJobRunnerSpec extends ActorTestClass with Matchers {
  override protected def beforeAll(): Unit = {
    super.beforeAll()
    ProcessService.nameToClassOpt = ProcessServiceSpec.safeProcessServiceNameToClassOpt
  }


  import scala.concurrent.ExecutionContext.Implicits.global

  val timeout = ConstVars.timeout1S * 4
  "Named Job Runner" should {
    "start new driver when name is different" in {
      val job1 = TestVars.sleepProcessJob
      BackendServer.runNamedProcessCommand(job1, "job11")
      val job2 = TestVars.processJob
      val resultFuture = BackendServer.runNamedProcessCommand(job2, "job12")
      val result = Await.ready(resultFuture, timeout)
      println(result)
    }

    "Named Job Runner" should {
      "start new driver when name is different but will failed without fsm " in {
        val job1 = TestVars.sleepProcessJob
        BackendServer.runNamedProcessCommand(job1, "job1")
        val job2 = TestVars.processJob
        BackendServer.changeFSMForNamedJob("job2", -1)
        val resultFuture = BackendServer.runNamedProcessCommand(job2, "job2")
        var timeOut = false
        try {
          val result = Await.ready(resultFuture, timeout)
          println(result)
        } catch {
          case exception: Exception =>
            timeOut = true
            println(exception)
        }
        timeOut shouldBe (true)
      }
    }

    "Name Job with Meta" must{
      "run job in sequece the sleep operation will block following operation and time out will go" in{
        val job1 = TestVars.sleepProcessJob
        BackendServer.runNamedProcessCommand(job1, "job13",metaOpt = Some(JobMeta("NewNameJob1")))
        val job2         = TestVars.processJob
        val resultFuture = BackendServer.runNamedProcessCommand(job2, "job13",metaOpt = Some(JobMeta("NewNameJob2")))
        val result       =
          try{
            Await.ready(resultFuture, timeout)
          }catch {
            case _:Throwable =>Future("TimeOutError")
          }
        result.map{
          a =>
            a shouldBe("TimeOutError")
            println(a)
        }
      }
    }

    "use same driver when name same" in {
      val job1 = TestVars.sleepProcessJob
      val result1Future = BackendServer.runNamedProcessCommand(job1, "job3")
      val job2 = TestVars.processJob
      val resultFuture = BackendServer.runNamedProcessCommand(job2, "job3")
      var timeOut = false

      try {
        val result = Await.ready(resultFuture, timeout)
        println(result)
      } catch {
        case exception: Exception =>
          timeOut = true
          println(exception)
      }
      timeOut shouldBe (true)
    }


    "use same driver when name same with increased fsm " in {
      val job1 = TestVars.sleepProcessJob
      BackendServer.changeFSMForNamedJob("job4", 1)
      val result1Future = BackendServer.runNamedProcessCommand(job1, "job4")
      val job2 = TestVars.processJob
      val resultFuture = BackendServer.runNamedProcessCommand(job2, "job4")
      var timeOut = false

      try {
        val result = Await.ready(resultFuture, timeout)
        println(result)
      } catch {
        case exception: Exception =>
          timeOut = true
          println(exception)
      }
      timeOut shouldBe (false)
    }
  }
}
