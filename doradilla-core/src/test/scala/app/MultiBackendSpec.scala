package app

import akka.actor.ActorSystem
import akka.testkit.TestProbe
import doracore.ActorTestClass
import doracore.core.fsm.FsmActor
import doracore.core.fsm.FsmActor.SetDriver
import doracore.core.msg.Job.{JobMsg, JobRequest}
import doracore.core.queue.QueueActor.RequestList
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
      val backendServer = BackendServer.startup(Some(1600))
      backendServer.registFSMActor()
      val msg = TestVars.processCallMsgTest
      val backendServer2 = BackendServer.startup()
      val processJob = JobMsg("SimpleProcess", msg)
      val res = BackendServer.runProcessCommand(processJob, Some(backendServer2)).map { result =>
        println(result)
        assert(true)
      }
      Await.ready(res, ConstVars.timeout1S * 10)



      val msg2 = TestVars.sleepProcessCallMsgTest

      val processJob2 = JobMsg("SimpleProcess", msg2)
      val timeout = ConstVars.timeout1S *2
      try{
        val res = BackendServer.runProcessCommand(processJob2, Some(backendServer2),timeout)
        println("result")
        println(res)
      }catch {
        case ex:Throwable => println(ex)
      }
      try{
        val res = BackendServer.runProcessCommand(processJob2, Some(backendServer2),timeout*2)
        println("result")
        println(res)
      }catch {
        case ex:Throwable => println(ex)
      }
      try{
        val res = BackendServer.runProcessCommand(processJob2, Some(backendServer2),timeout*3)
        println("result")
        println(res)
      }catch {
        case ex:Throwable => println(ex)
      }
      val res2 = BackendServer.runProcessCommand(processJob, Some(backendServer2)).map { result =>
        println(result)
        assert(true)
      }
      Await.ready(res2, ConstVars.timeout1S * 10)

    }

    "timeout will be used in config" in {
      val config =Some(DoraConf.config(1888,"backend",Some("doradilla.fsm.timeout=3")))
      val systemTest = ActorSystem("testSSS", config)
      val proxy = TestProbe()
      val proxy2 = TestProbe()
      val fsmActor = systemTest.actorOf(FsmActor.fsmActorProps, "fsmtestass")
      fsmActor ! SetDriver(proxy.ref)
      val msg = TestVars.sleepProcessCallMsgTest
      val backendServer2 = BackendServer.startup()
      val processJob = JobMsg("SimpleProcess", msg)
      val requestMsg = JobRequest(processJob,proxy2.ref,proxy.ref)
      fsmActor ! requestMsg
      Thread.sleep(4000)
      val msg1 = TestVars.processCallMsgTest
      val processJob1 = JobMsg("SimpleProcess", msg1)
      val requestMsg2 = JobRequest(processJob1,proxy2.ref,proxy.ref)
      fsmActor ! requestMsg2
      Thread.sleep(1000)
    }
  }
}
