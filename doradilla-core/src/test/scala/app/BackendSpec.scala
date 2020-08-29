package app

import doracore.ActorTestClass
import doracore.core.msg.Job.{JobMsg, JobStatus}
import doracore.util.ProcessService.{ProcessResult, noImplementNameToClassOpt}
import doracore.util.{ProcessService, ProcessServiceSpec}
import doracore.vars.ConstVars
import doradilla.back.BackendServer
import doradilla.conf.TestVars
import org.scalatest.Matchers

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * For app in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/6/23
  */
class BackendSpec extends ActorTestClass with Matchers {
  override protected def beforeAll(): Unit = {
    super.beforeAll()

  }
  "Backend server " must {

    "start and run command " in {
      ProcessService.nameToClassOpt = ProcessServiceSpec.safeProcessServiceNameToClassOpt
      val msg = TestVars.processCallMsgTest
      val processJob = JobMsg("SimpleProcess", msg)
      val res = BackendServer.runProcessCommand(processJob).map {
        res =>
          println(res)
          assert(true)
      }
      Await.ready(res, ConstVars.timeout1S * 10)
      //backendServer.actorSystemOpt.get.terminate()
    }

    "start the command and qurey result " in {
      BackendServer.startup(Some(1600))
      BackendServer.getBackendServer()
      ProcessService.nameToClassOpt = ProcessServiceSpec.safeProcessServiceNameToClassOpt
      val msg = TestVars.processCallMsgTest
      val processJob = JobMsg("SimpleProcess", msg)
      val receiveActor = BackendServer.startProcessCommand(processJob).get
      val res= BackendServer.queryProcessResult(receiveActor).map {
        result =>
          (result.result.asInstanceOf[ProcessResult]).jobStatus shouldBe (JobStatus.Finished)
          println(result)
      }

      Await.ready(res, ConstVars.timeout1S*4)
    }

    "start the command and qurey result without implement class reflector " in {
      ProcessService.nameToClassOpt = noImplementNameToClassOpt
      val msg = TestVars.processCallMsgTest
      val processJob = JobMsg("SimpleProcess", msg)
      val receiveActor = BackendServer.startProcessCommand(processJob).get
      val res= BackendServer.queryProcessResult(receiveActor).map {
        result =>
          (result.result.asInstanceOf[ProcessResult]).jobStatus shouldBe (JobStatus.Failed)
          println(result)
      }

      Await.ready(res, ConstVars.timeout1S*4)
    }

  }
}
