package jobs.fib

import akka.actor.ActorRef
import doradilla.base.BaseActor
import doradilla.msg.TaskMsg._
import jobs.fib.FibnacciTranActor.{FibAdd, FibInit, FibOperation, FibRequest}
import play.api.libs.json.Json

/**
  * For jobs.jobs.fib in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/30
  */
class FibnacciTranActor extends BaseActor {
  implicit val FibRequestFormat = Json.format[FibRequest]
  def translateFibRequest(requestItem: RequestMsg, sender: ActorRef): Unit = {
    FibOperation.withDefaultName(requestItem.taskMsg.operation) match {
      case FibOperation.FibReq =>
        Json.parse(requestItem.taskMsg.data).asOpt[FibRequest] match {
          case Some(fibRequest) =>
            sender ! WorkerInfo(classOf[FibWorkActor].getName, Some(requestItem.taskMsg.data), Some(requestItem.replyTo))
            sender ! TranslatedTask(FibInit(FibAdd(1,1,0),requestItem.replyTo))
          case _ => sender ! TranslationError(Some(s"Request data failed to process: ${requestItem.taskMsg.data}"))
        }
      case _ => sender ! TranslationError(Some(s"Operation name failed to process: ${requestItem.taskMsg.operation}"))
    }
  }

  override def receive: Receive = {
    case requestitem: RequestMsg => translateFibRequest(requestitem, sender())
  }

}


object FibnacciTranActor {
  implicit val FibRequestFormat = Json.format[FibRequest]
  implicit val FibAddFormat = Json.format[FibAdd]
  implicit val FibResultFormat = Json.format[FibResult]

  object FibOperation extends Enumeration {
    type FibOperation = Value
    val FibReq, FibAdd, Finish, Unknown = Value

    def withDefaultName(name: String): Value =
      values.find(_.toString.toLowerCase == name.toLowerCase).getOrElse(Unknown)
  }

  case class FibAdd(a: Int, fa: Int, fam1: Int)

  case class FibRequest(a: Int)

  case class FibResult(a: Int, fa: Int)

  case class FibInit(fibadd:FibAdd, replyTo:ActorRef)

}
