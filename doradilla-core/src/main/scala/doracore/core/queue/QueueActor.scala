package doracore.core.queue

import doracore.base.BaseActor
import doracore.core.msg.Job.JobRequest
import QueueActor.{FetchTask, RequestList, RequestListResponse}
import akka.actor.{ActorLogging, ActorRef, Props}
import doracore.util.{ConfigService, DoraConfig}

/**
  * For doradilla.queue in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/24
  */
class QueueActor extends BaseActor with ActorLogging{
  var taskQueue:Quelike[JobRequest] = ConfigService.getStringOpt(DoraConfig.getConfig(),"doradilla.queue.type") match {
    case Some("Fifo") =>
      log.debug("QueueActor is using Fifo queue")
      new FifoQue[JobRequest]()
    case _=>
      log.debug("QueueActor is using Priority queue")
      new PriorityQue()
  }

  def insert(item: JobRequest) = {
    taskQueue.enqueue(item)
  }

  def fetch(num: Int): Seq[JobRequest] = {
    taskQueue.dequeue(num)
  }

  override def receive: Receive = {
    case item: JobRequest => insert(item)
    case FetchTask(num,requestActor) => sender() ! RequestListResponse(RequestList(fetch(num)),requestActor)
  }
}

object QueueActor {

  case class FetchTask(num: Int = 1, requestActor: ActorRef)

  case class RequestList(requests: Seq[JobRequest])

  case class RequestListResponse(requestList: RequestList, requestActor:ActorRef)

  val queueActorProps: Props = Props(new QueueActor())
}