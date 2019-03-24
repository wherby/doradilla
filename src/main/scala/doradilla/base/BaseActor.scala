package doradilla.base

import akka.actor.ActorLogging
import doradilla.base.BaseActor.NotHandleMessage
import doradilla.base.query.QueryActor

/**
  * For doradilla.base in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/23
  */
trait BaseActor extends QueryActor with ActorLogging{

  override def unhandled(message: Any): Unit = message match {
    case NotHandleMessage(e) => log.info(s"Not handled : $e")
    case e =>
      sender() ! NotHandleMessage(e)
      super.unhandled(e)
  }
}

object BaseActor{
  case class NotHandleMessage(msg:Any)
}