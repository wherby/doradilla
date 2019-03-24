package doradilla.queue

import doradilla.base.BaseActor
import doradilla.msg.TaskMsg.RequestItem
import doradilla.queue.QueueActor.{FetchTask, RequestList}

/**
  * For doradilla.queue in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/24
  */
class QueueActor extends BaseActor{
  var taskQueue:Seq[RequestItem]  = Seq()

  def insert(item: RequestItem)={
    taskQueue = item +: taskQueue
  }

  def fetch(num : Int) = {
    val res = taskQueue.take(num)
    taskQueue= taskQueue.drop(num)
    res
  }

  override def receive: Receive = {
    case item:RequestItem=> insert(item)
    case FetchTask(num) => sender()! RequestList(fetch(num))
  }
}

object QueueActor{
  case class FetchTask(num: Int =1)
  case class RequestList(requests : Seq[RequestItem])
}