package app

import io.github.wherby.doradilla.app.SimpleClusterApp
import io.github.wherby.doradilla.back.BackendServer
import io.github.wherby.doradilla.conf.TestVars
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * For io.github.wherby.doradilla.test in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/5/18
  */
object SimpleClusterAppTest {
  def main(args: Array[String]): Unit = {
    val serverSeq= SimpleClusterApp.RunWithArgs(Array())
    val msg = TestVars.processCallMsgTest
    BackendServer.runProcessCommand(msg,Some(serverSeq.head)).map{
      println
    }
  }
}
