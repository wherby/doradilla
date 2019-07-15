package app

import doradilla.back.BackendServer
import doradilla.conf.TestVars
import org.scalatest.{AsyncFlatSpec, Matchers}


/**
  * For app in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/6/23
  */
class BackendSpec extends AsyncFlatSpec  with  Matchers{
  "Baeckend server " should "start and run command " in {
    val backendServer = BackendServer.startup(Some(1600))
    backendServer.registFSMActor()
    val msg = TestVars.processCallMsgTest
    BackendServer.runProcessCommand(msg).map{
      res=> println(res)
        assert(true)
    }
  }

  "Run process Command " should  "start the command and qurey result " in {
    val backendServer = BackendServer.startup(Some(1600))
    backendServer.registFSMActor()
    val msg = TestVars.processCallMsgTest
    val receiveActor = BackendServer.startProcessCommand(msg).get
    BackendServer.queryProcessResult(receiveActor).map{
        resultOpt =>
          assert(resultOpt == None)
    }
    Thread.sleep(2000)
    BackendServer.queryProcessResult(receiveActor).map{
      resultOpt =>
        assert(resultOpt != None )
    }
  }
}
