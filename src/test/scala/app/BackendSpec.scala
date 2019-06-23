package app

import io.github.wherby.doradilla.back.BackendServer
import io.github.wherby.doradilla.conf.TestVars
import org.scalatest.{AsyncFlatSpec, FlatSpec, Matchers}


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
}
