package doradilla.core.msg

import akka.actor.ActorRef
import Job.JobStatus.JobStatus

/**
  * For doradilla.msg in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/24
  */
object Job {

  case class JobSetting(timeout: Int, retry: Int)

  case class JobMsg(operation: String, data: Any, taskControl: Option[JobSetting] = None)

  case class JobRequest(taskMsg: JobMsg, replyTo: ActorRef, tranActor: ActorRef, priority : Option[Int] = None)

  case class JobEnd(requestMsg: JobRequest)

  case class JobResult(taskStatus: JobStatus, result: Any)

  case class WorkerInfo(actorName: String, config: Option[String], replyTo: Option[ActorRef])

  object JobStatus extends Enumeration {
    type JobStatus = Value
    val Queued, Scheduled, Working, Finished, TimeOut, Failed, Unknown = Value

    def withDefaultName(name: String): Value = {
      values.find(_.toString.toLowerCase == name.toLowerCase).getOrElse(Unknown)
    }
  }

}
