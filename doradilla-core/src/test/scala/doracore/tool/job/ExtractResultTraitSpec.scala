package doracore.tool.job

import doracore.ActorTestClass
import doracore.core.msg.Job.JobStatus
import doracore.tool.job.worker.ExtractResultTrait
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future}


class ExtractResultTraitSpec  extends ActorTestClass with ExtractResultTrait{
  "Extract result " should{
    "return fail when future failed" in {
      val failedFuture = Future{throw new Exception("Ka-boom!")}
      Thread.sleep(100)
      extractJobResult(failedFuture.value.get).taskStatus should be (JobStatus.Failed)
    }
  }
}
