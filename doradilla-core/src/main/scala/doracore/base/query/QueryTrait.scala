package doracore.base.query

import akka.actor.{Actor, ActorLogging, ActorRef}
import doracore.base.query.QueryTrait.{ChildInfo, NotHandleMessage, QueryChild}
/**
  * For doradilla.base.query in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/23
  */
trait QueryTrait extends Actor with ActorLogging{
  def getChildren()={
    this.context.children.map{
      child =>child.path.toString
    }.toSeq
  }

  override def unhandled(message: Any): Unit = message match {
    case queryChild: QueryChild => val childInfo = ChildInfo(context.self.path.toString,getChildren(),System.currentTimeMillis()/1000)
      queryChild.actorRef ! childInfo
      this.context.children.map{ child=>
        child ! queryChild
      }
    case e=>
      log.debug(s"Not Handled $e  from $sender()")
      sender() ! NotHandleMessage(s"Not Handled $e  from $sender()")
  }
}
object QueryTrait{
  case class QueryChild(actorRef: ActorRef)
  case class ChildInfo(root:String, children: Seq[String], timestamp: Long)
  case class NotHandleMessage(msg:Any)
}