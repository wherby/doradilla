package doradilla.base.query

import akka.actor.{Actor, ActorRef}
import doradilla.base.query.QueryActor.{ChildInfo, QueryChild}
/**
  * For doradilla.base.query in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/23
  */
trait QueryActor extends Actor{
  def getChildren()={
    this.context.children.map{
      child =>child.path.toString
    }.toSeq
  }

  override def unhandled(message: Any): Unit = message match {
    case query@QueryChild(actorRef) => val childInfo = ChildInfo(context.self.path.toString,getChildren(),System.currentTimeMillis()/1000)
      actorRef ! childInfo
      context.children.map{ child=>
        child ! query
      }
    case e=>super.unhandled(e)
  }
}
object QueryActor{
  case class QueryChild(actorRef: ActorRef)
  case class ChildInfo(root:String, children: Seq[String], timestamp: Long)
}