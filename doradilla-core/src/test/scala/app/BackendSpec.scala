package app

import doracore.ActorTestClass
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
  "Baeckend server " must {

    "start and run command " in {
      val backendServer = BackendServer.startup(Some(1600))
      backendServer.registFSMActor()
      val msg = TestVars.processCallMsgTest
      val res = BackendServer.runProcessCommand(msg).map {
        res =>
          println(res)
          assert(true)
      }
      Await.ready(res, ConstVars.timeout1S * 10)
    }

    "start the command and qurey result " in {
      val backendServer = BackendServer.startup(Some(1600))
      backendServer.registFSMActor()
      val msg = TestVars.processCallMsgTest
      val receiveActor = BackendServer.startProcessCommand(msg).get
      BackendServer.queryProcessResult(receiveActor).map {
        resultOpt =>
          assert(resultOpt == None)
      }
      Thread.sleep(2000)
      val res = BackendServer.queryProcessResult(receiveActor).map {
        resultOpt =>
          assert(resultOpt != None)
      }
      Await.ready(res, ConstVars.timeout1S)
    }

    "start and run command without actor  " in {
      val backendServer = BackendServer.startup(Some(1600))
      backendServer.registFSMActor()
      backendServer.actorMap = Map()
      val msg = TestVars.processCallMsgTest
      val res = BackendServer.runProcessCommand(msg).map {
        res =>
          println(res)
      }
      Await.ready(res, ConstVars.timeout1S * 10)
    }
  }
}
