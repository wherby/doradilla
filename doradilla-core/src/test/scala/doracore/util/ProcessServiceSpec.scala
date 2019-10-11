package doracore.util

import doracore.core.msg.Job.JobStatus
import doracore.util.ProcessService.{ProcessCallMsg, ProcessResult}
import doracore.vars.ConstVars
import org.scalatest._
import vars.ConstVarTest

import scala.concurrent.Await


/**
  * For doradilla.util in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/23
  */
class ProcessServiceSpec extends FlatSpec with Matchers {
  val processCallMsg = ProcessCallMsg("doracore.util.TestProcessor","addPar",Array(Par1(2).asInstanceOf[AnyRef],Par2(4).asInstanceOf[AnyRef]))
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

  "Process service" should "return left for Command sercice call failed " in {
    val msg = ConstVarTest.processCallMsgTest.copy(clazzName = "NOExisted")
    val resultFuture = ProcessService.callProcess(msg)
    resultFuture.isRight should be (false)
  }

  "Process service" should "return failed when call processReusult for wrong process execution " in {
    val msg = ConstVarTest.processCallMsgTest.copy(clazzName = "NOExisted")
    val resultFuture = Await.result( ProcessService.callProcessResult(msg), ConstVars.timeout1S)
    resultFuture.jobStatus should be (JobStatus.Failed)
  }

  "Process service" should "return result for Command sercice call in object" in {
    val msg = processCallMsg.copy( clazzName = "doracore.util.TestProcessor2",methodName = "objectAdd")
    val result = ProcessService.callProcess(msg)
    result shouldBe(Right(Par1(6)))
  }

  "Process Service " should "return left  in SimpleProcessFuture use with wrong parameters" in{
    val msg = processCallMsg.copy( clazzName = "doracore.util.TestProcessor",methodName = "addFuture" ,paras = Array(2.asInstanceOf[AnyRef],4.asInstanceOf[AnyRef]))
    val result = ProcessService.callProcessAwaitFuture(msg)
    result.isLeft should be (true)
  }
  "Process Service " should "return futureResult in SimpleProcessFuture use" in{
    val msg = processCallMsg.copy( clazzName = "doracore.util.TestProcessor",methodName = "addFuture")
    val result = ProcessService.callProcessAwaitFuture(msg)
    result shouldBe(Right(Par1(6)))
  }

  "Process Service " should "return futureResult in callProcessFutureResult use" in{
    val msg = processCallMsg.copy( clazzName = "doracore.util.TestProcessor",methodName = "addFuture")
    val result = Await.result( ProcessService.callProcessFutureResult(msg), ConstVars.timeout1S)
    result shouldBe(ProcessResult(JobStatus.Finished,Par1(6)))
  }

  "Process Service " should "return futureResult with failed  in callProcessFutureResult use when use wrong classname " in{
    val msg = processCallMsg.copy( clazzName = "doracore.util.TestProcesso3",methodName = "addFuture")
    val result = Await.result( ProcessService.callProcessFutureResult(msg), ConstVars.timeout1S)
    result shouldBe(ProcessResult(JobStatus.Failed,"Only processor with name Processor will be created."))
  }

  "Process Service " should "return left if class name is not with Processor " in{
    val msg = processCallMsg.copy( clazzName = "doracore.util.TestProcesso3",methodName = "addFuture")
    val result = ProcessService.callProcessAwaitFuture(msg)
    result shouldBe(Left("Only processor with name Processor will be created."))
  }
}


