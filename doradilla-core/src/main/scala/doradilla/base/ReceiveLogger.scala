package doradilla.base

import akka.actor.{Actor, ActorLogging}

/**
  * For doradilla.base in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/13
  */
trait ReceiveLogger {
  this: Actor with ActorLogging =>

  def logMessage: Receive = new Receive {
    def isDefinedAt(x: Any) = {
      log.info(s"Got a $x")
      false
    }
    def apply(x: Any) = throw new UnsupportedOperationException
  }
}
