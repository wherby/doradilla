package doradilla.msg

import akka.actor.ActorRef

/**
  * For doradilla.msg in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/24
  */
object TaskMsg {

  case class TaskControl(timeout: Int, retry: Int)

  case class TaskMsg(operation: String, data: String, taskControl: Option[TaskControl] = None)

  case class RequestMsg(taskMsg: TaskMsg, replyTo: ActorRef, tranActor: ActorRef)

  case class EndRequest(requestMsg: RequestMsg)

  case class TaskResult(result: String)

  case class WorkerInfo(actorName: String, config: Option[String])

  case class TranslationError(info: Option[String])

  object TaskStatus extends Enumeration {
    type TaskStatus = Value
    val Queued, Scheduled,  Working, Finished, TimeOut, Failed, Unknown = Value

    def withDefaultName(name: String): Value = {
      values.find(_.toString.toLowerCase == name.toLowerCase).getOrElse(Unknown)
    }
  }

  trait ChildActorMsg

}
