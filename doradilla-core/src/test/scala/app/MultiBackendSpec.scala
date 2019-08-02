package app

import doracore.ActorTestClass
import doracore.core.msg.Job.JobMsg
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
class MultiBackendSpec extends ActorTestClass with Matchers {
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
    }
  }
}
