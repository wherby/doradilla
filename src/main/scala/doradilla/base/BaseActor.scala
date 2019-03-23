package doradilla.base

import akka.event.slf4j.Logger
import doradilla.base.BaseActor.NotHandleMessage
import doradilla.base.query.QueryActor

/**
  * For doradilla.base in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/23
  */
trait BaseActor extends QueryActor{

  override def unhandled(message: Any): Unit = message match {
    case NotHandleMessage(e) => Logger
    case e => sender() ! NotHandleMessage(e)
      super.unhandled(e)
  }
}

object BaseActor{
  case class NotHandleMessage(msg:Any)
}