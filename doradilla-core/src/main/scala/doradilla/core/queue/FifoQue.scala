package doradilla.core.queue

/**
  * For doradilla.core.queue in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/6/11
  */
class FifoQue[T] extends Quelike[T] {
  var queue:Seq[T] = Seq()
  override def enqueue(ele: T): Unit = {
    queue = queue :+ ele
  }

  override def dequeue(number: Int): Seq[T] = {
    val res = queue.take(number)
    queue = queue.drop(number)
    res
  }
}
