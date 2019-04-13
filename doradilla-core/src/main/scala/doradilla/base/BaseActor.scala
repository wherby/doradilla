package doradilla.base

import akka.actor.ActorLogging
import doradilla.base.query.QueryTrait
import doradilla.base.query.QueryTrait.NotHandleMessage

/**
  * For doradilla.base in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/23
  */
trait BaseActor extends QueryTrait with ActorLogging with ReceiveLogger{

  override def unhandled(message: Any): Unit = message match {
    case NotHandleMessage(e) => log.info(s"Not handled : $e")
    case e =>
      super.unhandled(e)
  }
}

object BaseActor{

}