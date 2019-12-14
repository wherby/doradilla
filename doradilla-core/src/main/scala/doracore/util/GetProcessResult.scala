package doracore.util

import doracore.core.msg.Job.JobStatus
import doracore.util.ProcessService.{ProcessCallMsg, ProcessResult, callProcess}

import scala.concurrent.{ExecutionContext, Future}

/**
  * For doracore.util in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/12/14
  */
trait GetProcessResult {
  def callProcessResult(processCallMsg: ProcessCallMsg)(implicit executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global): Future[ProcessResult] = {
    val fx = ProcessService.getProcessMethod(processCallMsg)
    Future {
      callProcessResultSync(processCallMsg, fx)
    }(executor)
  }

  private def callProcessResultSync(processCallMsg: ProcessCallMsg, fx: ProcessCallMsg => Either[AnyRef, AnyRef] = callProcess): ProcessResult = {
    fx(processCallMsg) match {
      case Right(x) =>
        ProcessResult(JobStatus.Finished, x)
      case Left(y) => ProcessResult(JobStatus.Failed, y)
    }
  }
}
