package doradilla.conf


import com.typesafe.config.{Config, ConfigFactory}

/**
  * For io.github.wherby.doradilla.conf in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/5/11
  */
object DoraConf {
  lazy val config = ConfigFactory.load().getConfig("dora")

  def config(port: Int, role: String, testConfOption: Option[String] = None): Config = {
    val cfgTemp = ConfigFactory.parseString(
      s"""
      dora.akka.remote.artery.canonical.port=$port
      dora.akka.cluster.roles=[$role]
    """).withFallback(ConfigFactory.load()).getConfig("dora")
    val cfg = testConfOption match {
      case Some(testConf) => ConfigFactory.parseString(testConf).withFallback(cfgTemp)
      case _ => cfgTemp
    }

    println(cfg.toString)
    cfg
  }

}
