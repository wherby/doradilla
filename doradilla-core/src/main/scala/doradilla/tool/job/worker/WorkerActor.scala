package doradilla.tool.job.worker

import akka.actor.{ActorRef, Cancellable}
import doradilla.base.BaseActor
import doradilla.core.msg.Job.{JobResult, JobStatus}
import doradilla.core.msg.WorkerMsg.TickMsg
import doradilla.util.ProcessService.ExecuteResult
import doradilla.vars.ConstVars
import play.api.libs.json.Json
import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * For doradilla.tool.job.worker in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/13
  */
class WorkerActor extends BaseActor {
  var replyToOpt: Option[ActorRef] = None
  implicit val dispatcherToUse = context.system.dispatcher
  var futureResultOpt: Option[Future[ExecuteResult]] = None
  var cancelableSchedulerOpt: Option[Cancellable] = None
  val tickTime = ConstVars.tickTime


  def cancelScheduler():Option[Boolean] = {
    cancelableSchedulerOpt.map({
      cancelableScheduler => cancelableScheduler.cancel()
    })
  }

  def doSuccess(executeResult: ExecuteResult):Option[Unit] = {
    cancelScheduler()
    replyToOpt.map {
      replyTo =>
        val jobResult = executeResult.exitValue match {
          case 0 => JobResult(JobStatus.Finished, Json.toJson(executeResult).toString)
          case _ => JobResult(JobStatus.Failed, Json.toJson(executeResult).toString)
        }
        replyTo ! jobResult
    }
  }

  def handleTickMsg():Option[Any] = {
    futureResultOpt.map {
      futureResult =>
        futureResult.value match {
          case Some(Success(result)) => doSuccess(result)
          case Some(Failure(failure)) => doSuccess(ExecuteResult(-1, "", s"Thread failed. ResultFailed for : ${failure.getCause.getMessage}"))
          case None =>
        }
    }
  }

  override def receive: Receive = {
    case msg: TickMsg => handleTickMsg()
  }
}
