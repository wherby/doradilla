package doracore.tool.job.worker

import akka.actor.{ActorRef, Cancellable}
import doracore.base.BaseActor
import doracore.core.msg.Job.{JobResult, JobStatus}
import doracore.core.msg.WorkerMsg.TickMsg
import doracore.vars.ConstVars
import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * For doradilla.tool.job.worker in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/13
  */
class WorkerActor extends BaseActor {
  var replyToOpt: Option[ActorRef] = None
  implicit val dispatcherToUse = context.system.dispatcher
  var futureResultOpt: Option[Future[Any]] = None
  var cancelableSchedulerOpt: Option[Cancellable] = None
  val tickTime = ConstVars.tickTime


  def cancelScheduler():Option[Boolean] = {
    cancelableSchedulerOpt.map({
      cancelableScheduler => cancelableScheduler.cancel()
    })
  }

  def doSuccess(executeResultEither: Either[Any,Any]):Option[Unit] = {
    cancelScheduler()
    replyToOpt.map {
      replyTo =>
        val jobResult = executeResultEither match {
          case Right(executeResult) => JobResult(JobStatus.Finished,executeResult)
          case Left(value) =>JobResult(JobStatus.Failed,value)
        }
        replyTo ! jobResult
    }
  }

  def handleTickMsg():Option[Any] = {
    futureResultOpt.map {
      futureResult =>
        futureResult.value match {
          case Some(Success(result)) => doSuccess(Right(result))
          case Some(Failure(failure)) => doSuccess(Left(failure))
          case None =>
        }
    }
  }

  override def receive: Receive = {
    case msg: TickMsg => handleTickMsg()
  }
}