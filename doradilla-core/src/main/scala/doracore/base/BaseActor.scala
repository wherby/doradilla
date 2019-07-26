package doracore.base

import akka.actor.{Actor, ActorLogging}
import doracore.base.query.QueryTrait
import doracore.base.query.QueryTrait.NotHandleMessage

/**
  * For doradilla.base in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/23
  */
trait BaseActor extends QueryTrait with ActorLogging {
  this: Actor =>
  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.info(s"For ${reason} actor need to be restart")
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
