package doradilla.util

import doradilla.util.ProcessService.ProcessCallMsg
import org.scalatest._
import vars.ConstVarTest


/**
  * For doradilla.util in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/23
  */
class ProcessServiceSpec extends FlatSpec with Matchers {
  val processCallMsg = ProcessCallMsg("doradilla.util.TestProcess","addPar",Array(Par1(2).asInstanceOf[AnyRef],Par2(4).asInstanceOf[AnyRef]))
  "Process Service" should "return value "in {
    val result = ProcessService.callProcess(processCallMsg)
    result shouldBe(Right(Par1(6)))
  }

  "Process service" should "return left when class is not exist" in {
    val msg =processCallMsg.copy(clazzName = "NOTEXISTED")
    val result = ProcessService.callProcess(msg)
    result.isLeft should be (true)
  }

  "Process service" should "return left when method is not exist" in {
    val msg = processCallMsg.copy(methodName = "NotExisted")
    val result = ProcessService.callProcess(msg)
    result.isLeft should be (true)
  }

  "Process service" should "return left when paras is not correct" in {
    val msg = processCallMsg.copy(paras=Array(Par1(2).asInstanceOf[AnyRef]))
    val result = ProcessService.callProcess(msg)
    result.isLeft should be (true)
  }

  "Process service" should "return result for Command sercice call" in {
    val msg = ConstVarTest.processCallMsgTest
    val resultFuture = ProcessService.callProcess(msg)
    resultFuture.isRight should be (true)
  }


  "Process service" should "return result for Command sercice call in object" in {
    val msg = processCallMsg.copy( clazzName = "doradilla.util.TestProcess2",methodName = "objectAdd")
    val result = ProcessService.callProcess(msg)
    result shouldBe(Right(Par1(6)))
  }
}


