package doradilla.core.queue

import doradilla.core.msg.Job.{JobMsg, JobRequest}
import org.scalatest.{FlatSpec, Matchers}

/**
  * For doradilla.core.queue in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/6/11
  */
class PriorityQueSpec extends FlatSpec with Matchers {

  val task1 = JobRequest(JobMsg("test1",null,None),null,null,Some(9))
  val task2 = JobRequest(JobMsg("test2",null,None),null,null,Some(11))
  val task3 = JobRequest(JobMsg("test2",null,None),null,null,None)

  "PrioriryQue" must "return value in priority way" in{
    val priorityQue = new PriorityQue()
    priorityQue.enqueue(task1)
    priorityQue.enqueue(task2)
    priorityQue.enqueue(task3)
    val res = priorityQue.dequeue(1)
    res.head.priority should be (Some(11))
  }

  "PrioriryQue" must "return min value in latest" in{
    val priorityQue = new PriorityQue()
    priorityQue.enqueue(task1)
    priorityQue.enqueue(task3)
    priorityQue.enqueue(task2)
    priorityQue.dequeue(2)
    val res = priorityQue.dequeue(1)
    res.head.priority should be (None)
  }
  "PrioriryQue" must "could take more numbser " in{
    val priorityQue = new PriorityQue()
    priorityQue.enqueue(task1)
    priorityQue.enqueue(task3)
    priorityQue.enqueue(task2)
    priorityQue.dequeue(20)
    val res = priorityQue.dequeue(1)
    res should be (Seq())
  }
}
