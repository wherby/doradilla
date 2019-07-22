package doracore.core.queue

import org.scalatest.{FlatSpec, Matchers}

/**
  * For doradilla.core.queue in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/6/11
  */
class FifoQueSpec extends FlatSpec with Matchers {
  "Fifo " should "insert and retrive in same order" in {
    val fifo = new FifoQue[Int]()
    fifo.enqueue(3)
    fifo.enqueue(2)
    val a1 = fifo.dequeue(1)
    a1 should be(Seq(3))
    val a2 = fifo.dequeue(2)
    a2.length should be(1)
    a2 should be(Seq(2))
  }

  "Fifo" should "remove element and take snap" in {
    val fifo = new FifoQue[Int]()
    fifo.enqueue(3)
    fifo.enqueue(2)
    fifo.enqueue(3)
    fifo.enqueue(2)
    val a1 = fifo.removeEle(3)
    a1 should be(Seq(3,3))
    val b1 = fifo.snap()
    b1 should be(Seq(2,2))
  }

}
