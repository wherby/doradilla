package io.github.wherby.doradilla.conf

import com.typesafe.config.{Config, ConfigFactory}

/**
  * For io.github.wherby.doradilla.conf in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/5/11
  */
object DoraConf {
  lazy val config = ConfigFactory.load()

  def config(port: Int, role: String): Config =
    ConfigFactory.parseString(
      s"""
      dora.akka.remote.netty.tcp.port=$port
      dora.akka.cluster.roles=[$role]
    """).withFallback(ConfigFactory.load()).getConfig("dora")
}
