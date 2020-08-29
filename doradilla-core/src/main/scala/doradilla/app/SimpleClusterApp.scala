package doradilla.app

import akka.event.slf4j.Logger
import doracore.util.ConfigService
import doradilla.back.BackendServer
import doradilla.conf.DoraConf

/**
  * For io.github.wherby.doradilla.app in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/5/18
  */
/*object SimpleClusterApp {
  def main(args: Array[String]): Unit = {
    runWithArgs(args)
  }

  def runWithArgs(args: Array[String]): Seq[BackendServer] = {
    if (args.isEmpty) {
      lazy val port = ConfigService.getIntOpt( DoraConf.config, "cluster-setting.seed-port").getOrElse(1600)
      Seq(startApp(port))
    } else {
      try {
        args.map {
          arg => startApp(arg.toInt)
        }
      } catch {
        case e: Exception => Logger.apply(this.toString).error(s"Can't start the system with parameters :${args}for :${e.getMessage}")
          Seq()
      }
    }
  }

  def startApp(port: Int): BackendServer = {
    val backendServer = BackendServer.startup(Some(port))
    backendServer.registFSMActor()
    backendServer
  }
}*/
