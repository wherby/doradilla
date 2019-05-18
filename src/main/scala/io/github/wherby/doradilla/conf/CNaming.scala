package io.github.wherby.doradilla.conf

import com.datastax.driver.core.utils.UUIDs

/**
  * For io.github.wherby.doradilla.conf in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/5/18
  */
object CNaming {
  def timebasedName(actorname:String):String={
    actorname + UUIDs.timeBased().toString
  }
}
