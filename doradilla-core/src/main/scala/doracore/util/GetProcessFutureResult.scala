package doracore.util

import doracore.core.msg.Job.JobStatus
import doracore.util.ProcessService.{ProcessCallMsg, ProcessResult, callProcess}
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * For doracore.util in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/12/14
  */
trait GetProcessFutureResult {
  val timeOutSet: Duration = 3600 seconds

  def callProcessFutureResult(processCallMsg: ProcessCallMsg, timeOut: Duration = timeOutSet)(implicit executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global): Future[ProcessResult] = {
    val fx = ProcessService.getProcessMethod(processCallMsg)
    Future {
      callProcessAwaitFuture(processCallMsg, timeOut, fx)
    }(executor)
  }

  def callProcessAwaitFuture(processCallMsg: ProcessCallMsg, timeOut: Duration = timeOutSet, fx: ProcessCallMsg => Either[AnyRef, AnyRef] = callProcess): ProcessResult = {
    fx(processCallMsg) match {
      case Left(e) => ProcessResult(JobStatus.Failed, e)
      case Right(resultF) => getFutureResult(resultF, timeOut)
    }
  }

  def getFutureResult(resultF: AnyRef, timeOut: Duration = timeOutSet): ProcessResult = {
    try {
      val futureResult = resultF.asInstanceOf[Future[AnyRef]]
      val result = Await.result(futureResult, timeOut)
      ProcessResult(JobStatus.Finished, result)
    } catch {
      case e: Throwable => println(e)
        ProcessResult(JobStatus.Failed, e)
    }
  }
}
