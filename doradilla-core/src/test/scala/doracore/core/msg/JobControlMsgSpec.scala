package doracore.core.msg

import doracore.ActorTestClass
import doracore.core.msg.Job.JobStatus

class JobControlMsgSpec  extends  ActorTestClass {
  "JobStatus enum " should{
    "Convert from string" in {
      val a = JobStatus.withDefaultName( "Queued")
      a should be(JobStatus.Queued)
    }
  }
}
