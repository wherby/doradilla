package app

import akka.actor.ActorSystem
import akka.testkit.TestProbe
import doracore.ActorTestClass
import doracore.core.fsm.FsmActor
import doracore.core.fsm.FsmActor.SetDriver
import doracore.core.msg.Job.{JobMsg, JobRequest}
import doracore.core.queue.QueueActor.RequestList
import doracore.util.ProcessService.ProcessResult
import doracore.util.{ProcessService, ProcessServiceSpec}
import doracore.vars.ConstVars
import doradilla.back.BackendServer
import doradilla.conf.{DoraConf, TestVars}
import org.scalatest.Matchers

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * For app in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/6/23
  */
class MultiBackendSpec extends ActorTestClass with Matchers {
  ProcessService.nameToClassOpt = ProcessServiceSpec.safeProcessServiceNameToClassOpt
  "MultiBackend" should {
    "accept and run command " in {
      val config = Some(DoraConf.config(1644, "backend", Some("doradilla.fsm.timeout=3")))
      val backendServer = BackendServer.startup(Some(1644), config)
      backendServer.registFSMActor()
      val msg = TestVars.processCallMsgTest
      val config2 = Some(DoraConf.config(1646, "backend", Some("doradilla.fsm.timeout=3")))
      val backendServer2 = BackendServer.startup(Some(1646), config2)
      val processJob = JobMsg("SimpleProcess", msg)
      val res = BackendServer.runProcessCommand(processJob, Some(backendServer2)).map { result =>
        println(result)
        assert(true)
      }
      Await.ready(res, ConstVars.timeout1S * 10)


      val msg2 = TestVars.sleepProcessCallMsgTest

      val processJob2 = JobMsg("SimpleProcess", msg2)
      val timeout = ConstVars.timeout1S * 2
      try {
        val res = BackendServer.runProcessCommand(processJob2, Some(backendServer2), timeout * 30).map {
          re =>
            println("FIRST>>>>>>")
            println(re)
        }
        println(res)
      } catch {
        case ex: Throwable => println(ex)
      }
      try {
        BackendServer.runProcessCommand(processJob2, Some(backendServer2), timeout * 20).map {
          jobResult =>
            println("SECOND>>>>>>")
            println(jobResult)
            if ((jobResult.result.asInstanceOf[ProcessResult]).jobStatus.toString == "Failed") {
              println("failed....")
              throw new RuntimeException(jobResult.result.asInstanceOf[ProcessResult].result.asInstanceOf[Exception].getMessage)
            }
            println(jobResult)
            println("cccccaaaaaaaaaa")
        }
      } catch {
        case ex: Exception =>
          println("Cdd")
          println(ex)
      }

      try {
        val res = BackendServer.runProcessCommand(processJob2, Some(backendServer2), timeout * 1)
        res.map {
          re =>
            println(">>>>.3rd result")
            println(re)
        }
      } catch {
        case ex: Exception => println(ex)
          println(">>>>>>>>>>third call")
      }
      val res2 = BackendServer.runProcessCommand(processJob, Some(backendServer2)).map { result =>
        println(result)
        assert(true)
      }
      Await.ready(res2, ConstVars.timeout1S * 20)
      println()
      backendServer2.actorSystemOpt.get.terminate()
      backendServer.actorSystemOpt.get.terminate()
    }

    "timeout will be used in config" in {
      val config = Some(DoraConf.config(1888, "backend", Some("doradilla.fsm.timeout=3")))
      val systemTest = ActorSystem("testSSS", config)
      val proxy = TestProbe()
      val proxy2 = TestProbe()
      val fsmActor = systemTest.actorOf(FsmActor.fsmActorProps, "fsmtestass")
      fsmActor ! SetDriver(proxy.ref)
      val msg = TestVars.sleepProcessCallMsgTest
      val backendServer2 = BackendServer.startup()
      val processJob = JobMsg("SimpleProcess", msg)
      val requestMsg = JobRequest(processJob, proxy2.ref, proxy.ref)
      fsmActor ! requestMsg
      Thread.sleep(4000)
      val msg1 = TestVars.processCallMsgTest
      val processJob1 = JobMsg("SimpleProcess", msg1)
      val requestMsg2 = JobRequest(processJob1, proxy2.ref, proxy.ref)
      fsmActor ! requestMsg2
      Thread.sleep(1000)
      systemTest.terminate()
    }
  }
}
