package doracore.core.queue

/**
  * For doradilla.core.queue in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/6/11
  */
trait Quelike[T] {
  def enqueue(ele: T)

  def dequeue(number: Int): Seq[T]

  def removeEle(ele: T): Seq[T]

  def snap(): Seq[T]
}
