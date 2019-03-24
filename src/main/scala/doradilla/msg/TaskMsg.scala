package doradilla.msg

import akka.actor.ActorRef

/**
  * For doradilla.msg in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/24
  */
object TaskMsg {
  case class TaskControl(timeout: Int, retry: Int)
  case class RequestMsg(operation: String, data:String, taskControl: Option[TaskControl])
  case class RequestItem(requestMsg: RequestMsg,actorRef: ActorRef)
}
