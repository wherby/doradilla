package doracore.core.queue

import doracore.core.msg.Job.{JobMsg, JobRequest}
import org.scalatest.{FlatSpec, Matchers}

/**
  * For doradilla.core.queue in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/6/11
  */
class PriorityQueSpec extends FlatSpec with Matchers {

  val task1 = JobRequest(JobMsg("test1",null),null,null,Some(9))
  val task2 = JobRequest(JobMsg("test2",null),null,null,Some(11))
  val task3 = JobRequest(JobMsg("test2",null),null,null,None)
  val task4 = JobRequest(JobMsg("test4",null),null,null,None)
  val task5 = JobRequest(JobMsg("test5",null),null,null,None)
  val task6 = JobRequest(JobMsg("test6",null),null,null,None)

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

  "PriorityQue" must "FIFO" in{
    val priorityQue = new PriorityQue()
    priorityQue.enqueue(task4)
    priorityQue.enqueue(task5)
    priorityQue.enqueue(task6)
    val res = priorityQue.dequeue(1)
    val res2 = priorityQue.dequeue(1)
    res.head should be (task4)
    res2.head should be (task5)
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

  "PriorityQue" must "remove element and take snap"  in{
    val priorityQue = new PriorityQue()
    priorityQue.enqueue(task1)
    priorityQue.enqueue(task3)
    priorityQue.enqueue(task2)
    priorityQue.enqueue(task1)
    priorityQue.enqueue(task3)
    priorityQue.enqueue(task2)
    val a1 = priorityQue.removeEle(task2)
    a1 should be(Seq(task2,task2))
    val a2 = priorityQue.snap()
    a2 should be(Seq(task1,task1,task3,task3))
  }
}
