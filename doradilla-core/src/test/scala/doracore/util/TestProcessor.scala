package doracore.util

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * For doradilla.util in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/23
  */
class TestProcessor {
  def add(a: Int, b: Int) = {
    a + b
  }


  def addPar(a: Par1, b: Par2) = {
    Par1(a.va + b.va)
  }

  def addFuture(a: Par1, b: Par2) = {
    Future( Par1(a.va + b.va))
  }

  def addFutureException(a: Par1, b: Par2) = {
    Future( Par1(a.va / b.va))
  }
}

case class Par1(va: Int)

case class Par2(va: Int)


