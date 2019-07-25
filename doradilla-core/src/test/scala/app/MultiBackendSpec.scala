package app

import doracore.ActorTestClass
import doracore.vars.ConstVars
import doradilla.back.BackendServer
import doradilla.conf.TestVars
import org.scalatest.Matchers

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random

/**
  * For app in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/6/23
  */
class MultiBackendSpec extends ActorTestClass with Matchers {
  "MultiBackend" should {
    "accept and run command " in {
      val randomInt =  Random.nextInt(1000)
      val backendServer = BackendServer.startup(Some(1600 + randomInt))
      backendServer.registFSMActor()
      val msg = TestVars.processCallMsgTest
      val backendServer2 = BackendServer.startup(Some(1601 + randomInt))
      val res = BackendServer.runProcessCommand(msg, Some(backendServer2)).map { result =>
        println(result)
        assert(true)
      }
      Await.ready(res, ConstVars.timeout1S * 10)
    }
  }
}
