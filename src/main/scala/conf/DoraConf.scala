package conf

import com.typesafe.config.{Config, ConfigFactory}

/**
  * For conf in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/5/11
  */
object DoraConf {
  val config = ConfigFactory.load()

  def config(port: Int, role: String): Config =
    ConfigFactory.parseString(s"""
      akka.remote.netty.tcp.port=$port
      akka.cluster.roles=[$role]
    """).withFallback(ConfigFactory.load())
}
