package doracore.util

import doracore.core.msg.Job.JobStatus
import doracore.util.ProcessService.{ProcessCallMsg, ProcessResult}
import doracore.vars.ConstVars
import org.scalatest._
import vars.ConstVarTest

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.concurrent.duration._
import scala.language.postfixOps
/**
  * For doradilla.util in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/23
  */
object ProcessServiceSpec {
  def processServiceNameToClassOpt(className: String, classLoaderOpt: Option[ClassLoader]): Option[Class[_]] = {
    classLoaderOpt match {
      case Some(classloader) if className.indexOf("Processor") >= 0 =>
        Some(Class.forName(className, false, classloader))
      case _ if className.indexOf("Processor") >= 0 =>
        Some(Class.forName(className))
      case _ => None
    }
  }

  val processNameSet: Map[String, Class[_]] = Map(
    "doracore.util.TestProcessor" -> (new TestProcessor).getClass,
    "doracore.util.TestProcessor2" -> (new TestProcessor2).getClass,
    "doracore.util.TestProcesso3" -> (new TestProcesso3).getClass,
    "doracore.util.CommandServiceProcessor" -> (new CommandServiceProcessor).getClass,
  )

  def safeProcessServiceNameToClassOpt(className: String, classLoaderOpt: Option[ClassLoader]): Option[Class[_]] = {
    processNameSet.get(className)
  }
}

class ProcessServiceSpec extends FlatSpec with Matchers {


  val processCallMsg = ProcessCallMsg("doracore.util.TestProcessor", "addPar", Array(Par1(2).asInstanceOf[AnyRef], Par2(4).asInstanceOf[AnyRef]))
  "Process Service" should "return left when use no implement " in {
    ProcessService.nameToClassOpt = ProcessService.noImplementNameToClassOpt
    val result = ProcessService.callProcess(processCallMsg)
    result shouldBe (Left("Class is not found."))
    ProcessService.nameToClassOpt = ProcessServiceSpec.safeProcessServiceNameToClassOpt
  }
  "Process Service" should "return value " in {
    ProcessService.nameToClassOpt = ProcessServiceSpec.safeProcessServiceNameToClassOpt
    val result = ProcessService.callProcess(processCallMsg)
    result shouldBe (Right(Par1(6)))
  }

  "Process service" should "return left when class is not exist" in {
    ProcessService.nameToClassOpt = ProcessServiceSpec.safeProcessServiceNameToClassOpt
    val msg = processCallMsg.copy(clazzName = "NOTEXISTED")
    val result = ProcessService.callProcess(msg)
    println(result)
    result.isLeft should be(true)
  }

  "Process service" should "return left when method is not exist" in {
    ProcessService.nameToClassOpt = ProcessServiceSpec.processServiceNameToClassOpt
    val msg = processCallMsg.copy(methodName = "NotExisted")
    val result = ProcessService.callProcess(msg)
    result.isLeft should be(true)
  }

  "Process service" should "return left when paras is not correct" in {
    ProcessService.nameToClassOpt = ProcessServiceSpec.processServiceNameToClassOpt
    val msg = processCallMsg.copy(paras = Array(Par1(2).asInstanceOf[AnyRef]))
    val result = ProcessService.callProcess(msg)
    result.isLeft should be(true)
  }

  "Process service" should "return result for Command sercice call" in {
    ProcessService.nameToClassOpt = ProcessServiceSpec.processServiceNameToClassOpt
    val msg = ConstVarTest.processCallMsgTest
    val resultFuture = ProcessService.callProcess(msg)
    resultFuture.isRight should be(true)
  }

  "Process service" should "return left for Command sercice call failed " in {
    ProcessService.nameToClassOpt = ProcessServiceSpec.processServiceNameToClassOpt
    val msg = ConstVarTest.processCallMsgTest.copy(clazzName = "NOExisted")
    val resultFuture = ProcessService.callProcess(msg)
    resultFuture.isRight should be(false)
  }

  "Process service" should "return failed when call processReusult for wrong process execution " in {
    ProcessService.nameToClassOpt = ProcessServiceSpec.processServiceNameToClassOpt
    val msg = ConstVarTest.processCallMsgTest.copy(clazzName = "NOExisted")
    val resultFuture = Await.result(ProcessService.callProcessResult(msg), ConstVars.timeout1S)
    resultFuture.jobStatus should be(JobStatus.Failed)
  }

  "Process service" should "return result when call processReusult for right process execution " in {
    ProcessService.nameToClassOpt = ProcessServiceSpec.processServiceNameToClassOpt
    val msg = ConstVarTest.processCallMsgTest
    val resultFuture = Await.result(ProcessService.callProcessResult(msg), ConstVars.timeout1S)
    resultFuture.jobStatus should be(JobStatus.Finished)
  }

  "Process service" should "return result when call processReusult for right process execution with instamces " in {
    ProcessService.nameToClassOpt = ProcessServiceSpec.processServiceNameToClassOpt
    val instS = new CommandServiceProcessor()
    val msg = ConstVarTest.processCallMsgTest
    val msg2 = msg.copy(instOpt = Some(instS))
    val resultFuture = Await.result(ProcessService.callProcessResult(msg2), ConstVars.timeout1S)
    resultFuture.jobStatus should be(JobStatus.Finished)
  }

  "Process service" should "return result for Command sercice call in object" in {
    ProcessService.nameToClassOpt = ProcessServiceSpec.processServiceNameToClassOpt
    val msg = processCallMsg.copy(clazzName = "doracore.util.TestProcessor2", methodName = "objectAdd")
    val result = ProcessService.callProcess(msg)
    result shouldBe (Right(Par1(6)))
  }

  "Process Service " should "return left  in SimpleProcessFuture use with wrong parameters" in {
    ProcessService.nameToClassOpt = ProcessServiceSpec.processServiceNameToClassOpt
    val msg = processCallMsg.copy(clazzName = "doracore.util.TestProcessor", methodName = "addFuture", paras = Array(2.asInstanceOf[AnyRef], 4.asInstanceOf[AnyRef]))
    val result = ProcessService.callProcessAwaitFuture(msg)
    result.jobStatus shouldBe (JobStatus.Failed)
  }
  "Process Service " should "return futureResult in SimpleProcessFuture use" in {
    ProcessService.nameToClassOpt = ProcessServiceSpec.processServiceNameToClassOpt
    val msg = processCallMsg.copy(clazzName = "doracore.util.TestProcessor", methodName = "addFuture")
    val result = ProcessService.callProcessAwaitFuture(msg)
    result shouldBe (ProcessResult(JobStatus.Finished, Par1(6)))
  }

  "Process Service " should "return futureResult in callProcessFutureResult use" in {
    ProcessService.nameToClassOpt = ProcessServiceSpec.processServiceNameToClassOpt
    val msg = processCallMsg.copy(clazzName = "doracore.util.TestProcessor", methodName = "addFuture")
    val result = Await.result(ProcessService.callProcessFutureResult(msg), ConstVars.timeout1S)
    result shouldBe (ProcessResult(JobStatus.Finished, Par1(6)))
  }

  "Process Service " should "return futureResult in callProcessFutureResult use when inst is not none" in {
    ProcessService.nameToClassOpt = ProcessServiceSpec.processServiceNameToClassOpt
    val instS = new TestProcessor()
    val msg = processCallMsg.copy(clazzName = "doracore.util.TestProcessor", methodName = "addFuture")
    val msg2 = msg.copy(instOpt = Some(instS))
    val result = Await.result(ProcessService.callProcessFutureResult(msg2), ConstVars.timeout1S)
    result shouldBe (ProcessResult(JobStatus.Finished, Par1(6)))
  }

  "Process Service " should "return futureResult with failed  in callProcessFutureResult use when use wrong classname " in {
    val msg = processCallMsg.copy(clazzName = "doracore.util.TestProcesso3", methodName = "addFuture")
    val result = Await.result(ProcessService.callProcessFutureResult(msg), ConstVars.timeout1S)
    result shouldBe (ProcessResult(JobStatus.Failed, "Class is not found."))
  }

  "Process Service " should "return left if class name is not with Processor " in {
    ProcessService.nameToClassOpt = ProcessServiceSpec.processServiceNameToClassOpt
    val msg = processCallMsg.copy(clazzName = "doracore.util.TestProcesso3", methodName = "addFuture")
    val result = ProcessService.callProcessAwaitFuture(msg)
    result shouldBe (ProcessResult(JobStatus.Failed, "Class is not found."))
  }

  "Process Service" should "return left  value for no implementation " in {
    ProcessService.nameToClassOpt = ProcessService.noImplementNameToClassOpt
    val result = ProcessService.callProcess(processCallMsg)
    result shouldBe (Left("Class is not found."))
  }

  "Get Future result" should "return Left when result is not Future" in {
    val result = ProcessService.getFutureResult("result", 3600 seconds)
    result.jobStatus shouldBe (JobStatus.Failed)
  }

  "Get Future result" should "return right when result is  Future" in {
    val result = ProcessService.getFutureResult(Future("result")(scala.concurrent.ExecutionContext.Implicits.global), 3600 seconds)
    result.jobStatus shouldBe (JobStatus.Finished)
  }
}


