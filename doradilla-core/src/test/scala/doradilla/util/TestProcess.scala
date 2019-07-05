package doradilla.util

/**
  * For doradilla.util in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/23
  */
class TestProcess {
  def add(a: Int, b: Int) = {
    a + b
  }


  def addPar(a: Par1, b: Par2) = {
    Par1(a.va + b.va)
  }
}

case class Par1(va: Int)

case class Par2(va: Int)


