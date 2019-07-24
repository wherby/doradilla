package app

import doradilla.app.SimpleClusterApp
import doradilla.back.BackendServer
import doradilla.conf.TestVars
import org.scalatest.{AsyncFlatSpec, Matchers}

/**
  * For app in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/6/23
  */
class SimpleClusterAppSpec extends AsyncFlatSpec  with  Matchers{
  "SimpleCluster " should "accept a request and run " in {
    val serverSeq= SimpleClusterApp.runWithArgs(Array())
    val msg = TestVars.processCallMsgTest
    BackendServer.runProcessCommand(msg,Some(serverSeq.head)).map{ result=>
      println(result)
      assert(true)
    }
  }
}
