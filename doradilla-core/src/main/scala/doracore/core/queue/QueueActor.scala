package doracore.core.queue

import doracore.base.BaseActor
import doracore.core.msg.Job.{JobRequest, JobResult, JobStatus}
import QueueActor._
import akka.actor.{ActorLogging, ActorRef, Props}
import doracore.util.{ConfigService, DoraCoreConfig}

/**
  * For doradilla.queue in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/24
  */
class QueueActor extends BaseActor with ActorLogging {
  println(context.system.settings.config)
  var taskQueue: Quelike[JobRequest] = ConfigService.getStringOpt(context.system.settings.config, "doradilla.queue.type") match {
    case Some("Fifo") =>
      log.debug("QueueActor is using Fifo queue")
      new FifoQue[JobRequest]()
    case _ =>
      log.debug("QueueActor is using Priority queue")
      new PriorityQue()
  }

  def insert(item: JobRequest) = {
    taskQueue.enqueue(item)
  }

  def fetch(num: Int): Seq[JobRequest] = {
    taskQueue.dequeue(num)
  }

  def takeSnap() = {
    sender() ! SnapResult(taskQueue.snap())
  }

  def handleRemove(jobRequest: JobRequest) = {
    val removedJob = taskQueue.removeEle(jobRequest)
    removedJob.map {
      job => job.replyTo ! JobResult(JobStatus.Canceled, s"Job: $job  which is canceled by user.")
    }
  }

  override def receive: Receive = {
    case item: JobRequest => insert(item)
    case FetchTask(num, requestActor) => sender() ! RequestListResponse(RequestList(fetch(num)), requestActor)
    case _: Snap => takeSnap()
    case removeJob: RemoveJob => handleRemove(removeJob.job)
  }
}

object QueueActor {

  case class FetchTask(num: Int = 1, requestActor: ActorRef)

  case class RequestList(requests: Seq[JobRequest])

  case class RequestListResponse(requestList: RequestList, requestActor: ActorRef)

  case class RemoveJob(job: JobRequest)

  case class Snap()

  case class SnapResult(queueJobs: Seq[JobRequest])

  val queueActorProps: Props = Props(new QueueActor())
}