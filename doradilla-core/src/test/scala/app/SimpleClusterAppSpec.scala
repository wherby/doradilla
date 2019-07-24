package app

import doracore.ActorTestClass
import doracore.vars.ConstVars
import doradilla.app.SimpleClusterApp
import doradilla.back.BackendServer
import doradilla.conf.TestVars
import org.scalatest.{Matchers}
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * For app in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/6/23
  */
class SimpleClusterAppSpec extends ActorTestClass with Matchers {
  "SimpleCluster " should {
    "accept a request and run " in {
      val serverSeq = SimpleClusterApp.runWithArgs(Array())
      val msg = TestVars.processCallMsgTest
      val res = BackendServer.runProcessCommand(msg, Some(serverSeq.head)).map { result =>
        println(result)
        assert(true)
      }
      Await.ready(res, ConstVars.timeout1S * 10)
    }
  }
}
