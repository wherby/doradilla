package doracore.util

import doracore.util.ProcessService.ProcessCallMsg
import org.scalatest.{FlatSpec, Matchers}

/**
  * For doracore.util in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/10/14
  */
class ProcessServiceNotImplementSpec  extends FlatSpec with Matchers {

  val processCallMsg = ProcessCallMsg("doracore.util.TestProcessor","addPar",Array(Par1(2).asInstanceOf[AnyRef],Par2(4).asInstanceOf[AnyRef]))
  "Process Service" should "return value "in {
    ProcessService.nameToClassOpt = ProcessService.notImplement
    val result = ProcessService.callProcess(processCallMsg)
    result shouldBe(Left("Class is not found."))
  }
}
