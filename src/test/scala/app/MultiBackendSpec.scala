package app

import io.github.wherby.doradilla.back.BackendServer
import io.github.wherby.doradilla.conf.TestVars
import org.scalatest.{AsyncFlatSpec, Matchers}

/**
  * For app in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/6/23
  */
class MultiBackendSpec  extends AsyncFlatSpec  with  Matchers{
  "MultiBackend" should "accept and run command " in{
    val backendServer = BackendServer.startup(Some(1600))
    backendServer.registFSMActor()
    val msg = TestVars.processCallMsgTest
    val backendServer2 = BackendServer.startup(Some(1601))
    BackendServer.runProcessCommand(msg,Some(backendServer2)).map{result=>
      println(result)
      assert(true)
    }
  }
}
