package doradilla.core.queue

import doradilla.core.msg.Job.JobRequest

import scala.collection.mutable

/**
  * For doradilla.core.queue in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/6/11
  */
class PriorityQue extends Quelike [JobRequest]{
  implicit val ord: Ordering[(JobRequest,Int)] = Ordering.by(_._2)

  val queue = mutable.PriorityQueue[(JobRequest,Int)]()
  override def enqueue(ele: JobRequest): Unit = {
    queue.enqueue((ele,ele.priority.getOrElse(4)))
  }

  override def dequeue(number: Int): Seq[JobRequest] = {
    var res:Seq[JobRequest] = Seq()
    for(i <- 1 to number){
      if(queue.length >0){
        res = res :+ queue.dequeue()._1
      }
    }
    res
  }
}