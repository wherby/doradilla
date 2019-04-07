package jobs.process

import akka.actor.{ActorRef, Cancellable}
import doradilla.base.BaseActor
import doradilla.core.msg.Job.{JobResult, JobStatus}
import doradilla.util.ProcessService
import doradilla.util.ProcessService.ExecuteResult
import jobs.process.ProcessTranActor.SimpleProcessInit
import jobs.process.ProcessWorkerActor.TickMsg
import play.api.libs.json.Json
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
  * For jobs.process in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/7
  */
class ProcessWorkerActor extends BaseActor{
  var replayToOpt: Option[ActorRef] = None
  implicit val dispatcherToUse = context.system.dispatcher
  var futureRusult:Future[ExecuteResult] = null
  var cancelableSchedulerOpt: Option[Cancellable] = None
  val tickTime = 100 milliseconds

  def handleProcessInit(simpleProcessInit: SimpleProcessInit)={
    futureRusult = ProcessService.runProcess(simpleProcessInit.processRequest.cmd, dispatcherToUse)
    replayToOpt = Some(simpleProcessInit.replyTo)
    cancelableSchedulerOpt = Some(context.system.scheduler.schedule(tickTime,tickTime,this.self,TickMsg()))
  }

  def cancelScheduler() ={
    cancelableSchedulerOpt.map{
      cancelableScheduler=>cancelableScheduler.cancel()
    }
  }

  def doSuccess(executeResult: ExecuteResult)={
    cancelScheduler()
    replayToOpt.map{
      replayTo =>
        val jobResult = executeResult.exitValue match {
          case 0 =>  JobResult(JobStatus.Finished,Json.toJson(executeResult).toString)
          case _=> JobResult(JobStatus.Failed,Json.toJson(executeResult).toString)
        }
        replayTo ! jobResult
    }
  }


  def handleTickMsg()={
    if(futureRusult != null){
      futureRusult.value match {
        case Some(Success(result)) => doSuccess(result)
        case Some(Failure(failure))=> doSuccess(ExecuteResult(-1,"",s"Thread failed. ResultFailed for : ${failure.getCause.getMessage}"))
        case None =>
      }
    }else{
      cancelScheduler()
    }
  }

  override def receive: Receive = {
    case simpleProcessInit: SimpleProcessInit => handleProcessInit(simpleProcessInit)
    case msg:TickMsg =>handleTickMsg()
  }
}
object ProcessWorkerActor{
  case class TickMsg()
}
