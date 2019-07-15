package jobs.fib

import akka.actor.{ActorRef, Props}
import doracore.base.BaseActor
import doracore.core.msg.Job._
import doracore.core.msg.TranslationMsg.{TranslatedTask, TranslationDataError, TranslationOperationError}
import jobs.fib.FibnacciTranActor.{FibAdd, FibInit, FibOperation, FibRequest}
import play.api.libs.json.Json

/**
  * For jobs.jobs.jobs.fib in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/30
  */
class FibnacciTranActor extends BaseActor {
  implicit val FibRequestFormat = Json.format[FibRequest]

  def translateFibRequest(jobRequest: JobRequest, sender: ActorRef): Unit = {
    FibOperation.withDefaultName(jobRequest.taskMsg.operation) match {
      case FibOperation.FibReq =>
        Json.parse(jobRequest.taskMsg.data.toString).asOpt[FibRequest] match {
          case Some(fibRequest) =>
            sender ! WorkerInfo(classOf[FibWorkActor].getName, Some(jobRequest.taskMsg.data.toString), Some(jobRequest.replyTo))
            sender ! TranslatedTask(FibInit(FibAdd(1, 1, 0), jobRequest.replyTo))
          case _ => sender ! TranslationDataError(Some(jobRequest.taskMsg.data))
        }
      case _ => sender ! TranslationOperationError(Some(jobRequest.taskMsg.operation))
    }
  }

  override def receive: Receive = {
    case jobRequest: JobRequest => translateFibRequest(jobRequest, sender())
  }

}


object FibnacciTranActor {
  val fibnacciTranActorProps = Props(new FibnacciTranActor())
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

  case class FibInit(fibadd: FibAdd, replyTo: ActorRef)

}
