package io.github.wherby.doradilla.test

import io.github.wherby.doradilla.back.BackendServer
import io.github.wherby.doradilla.conf.TestVars
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * For io.github.wherby.doradilla.test in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/5/18
  */
object BackendTest {
  def main(args: Array[String]): Unit = {
    val backendServer = BackendServer.startup(Some(1600))
    backendServer.registFSMActor()
    val msg = TestVars.processCallMsgTest
    BackendServer.runProcessCommand(msg).map{
      res=> println(res)
    }
  }
}
