package doracore.core.queue

import doracore.core.msg.Job.JobRequest

import scala.collection.mutable

/**
  * For doradilla.core.queue in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/6/11
  */
class PriorityQue extends Quelike[JobRequest] {
  implicit val ord: Ordering[(JobRequest, Int)] = Ordering.by(_._2)

  var queue = mutable.PriorityQueue[(JobRequest, Int)]()

  override def enqueue(ele: JobRequest): Unit = {
    queue.enqueue((ele, ele.priority.getOrElse(4)))
  }

  override def dequeue(number: Int): Seq[JobRequest] = {
    var res: Seq[JobRequest] = Seq()
    for (i <- 1 to number) {
      if (queue.length > 0) {
        res =queue.take(1).map{
          pair=>pair._1
        }.toSeq
        queue =queue.drop(1)
      }
    }
    res
  }

  override def removeEle(ele: JobRequest): Seq[JobRequest] = {
    val res = queue.filter { job => job._1 == ele }.map {
      job => job._1
    }.toSeq
    queue = queue.filter { job => job._1 != ele }
    res
  }

  override def snap(): Seq[JobRequest] = {
    queue.map {
      job => job._1
    }.toSeq
  }
}
