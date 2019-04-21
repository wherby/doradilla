package doradilla.base

import akka.actor.{Actor, ActorLogging}
import akka.event.LoggingReceive
import doradilla.base.query.QueryTrait
import doradilla.base.query.QueryTrait.NotHandleMessage

/**
  * For doradilla.base in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/23
  */
trait BaseActor extends QueryTrait with ActorLogging with ReceiveLogger {
  this: Actor =>
  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.info(s"For ${reason.getMessage} actor need to be restart")
    super.preRestart(reason,message) // stops all children, calls postStop( ) for crashing actor
    log.info(s"actor restarting...")
  }
  override def postRestart(reason: Throwable): Unit = log.debug(s"actor restarted...")
  override def postStop(): Unit = log.debug(s"actor stopping...")

  override def unhandled(message: Any): Unit = message match {
    case NotHandleMessage(e) => log.info(s"Not handled : $e")
    case e =>
      super.unhandled(e)
  }
}
