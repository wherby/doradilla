package doracore.tool.query

import akka.actor.{ActorRef, Props}
import akka.event.LoggingReceive
import doracore.base.BaseActor
import doracore.base.query.QueryTrait.{ChildInfo, QueryChild}
import doracore.tool.query.QueryActor.{GetRoot, QueryRoot, RootResult}

/**
  * For doradilla.tool.query in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/5
  */
class QueryActor extends BaseActor {
  var childMap: Map[String, ChildInfo] = Map()

  def hundleChildInfo(childInfo: ChildInfo) = {
    childMap = childMap + (childInfo.root -> childInfo)
    childMap
  }

  def getChildren(path: String) = {
    childMap.get(path) match {
      case Some(child) => Map(path -> childMap.get(path).get)
      case None => Map[String, ChildInfo]()
    }
  }

  def hundleGetRoot(getRoot: GetRoot) = {
    getRoot.rootPath match {
      case None => childMap
      case Some(path) => getChildren(path)
    }
  }

  override def receive: Receive = LoggingReceive{
    case queryRoot: QueryRoot =>
      queryRoot.rootActor ! QueryChild(this.self)
    case childInfo: ChildInfo => hundleChildInfo(childInfo)
    case getRoot: GetRoot => sender() ! RootResult(hundleGetRoot(getRoot))
  }
}

object QueryActor {
  val queryActorProps = Props(new QueryActor())

  case class QueryRoot(rootActor: ActorRef)

  case class GetRoot(rootPath: Option[String] = None)

  case class RootResult(rootMap: Map[String, ChildInfo])

}
