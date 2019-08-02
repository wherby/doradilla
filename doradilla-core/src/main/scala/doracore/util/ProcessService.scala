package doracore.util

import doracore.core.msg.Job.JobStatus
import doracore.core.msg.Job.JobStatus.JobStatus

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

/**
  * For doradilla.util in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/22
  */
object ProcessService {
  var classLoaderOpt: Option[ClassLoader] = None
  def callProcess(processCallMsg: ProcessCallMsg) = {
    //Call reflection will takes time
    // get runtime universe
    val ru = scala.reflect.runtime.universe
    // get runtime mirror
    val rm = ru.runtimeMirror(getClass.getClassLoader)
    //val instanceMirror = rm.reflectClass(Class.)
    try {
      lazy val a = classLoaderOpt match {
        case Some(classloader) => Class.forName(processCallMsg.clazzName,false,classloader)
        case _=> Class.forName(processCallMsg.clazzName)
      }
      val instance = a.newInstance()
      val methodOpt = a.getMethods.filter(method => method.getName == processCallMsg.methodName).headOption
      methodOpt match {
        case Some(method) => Right(method.invoke(instance, processCallMsg.paras: _*))
        case _ => Left(new Throwable("No such method"))
      }
    } catch {
      case e: Throwable => println(e)
        Left(e)
    }
  }

  def callProcessAwaitFuture(processCallMsg: ProcessCallMsg) ={
    callProcess(processCallMsg) match {
      case Left(e) => Left(e)
      case Right(resultF) => try{
        val futureResult= resultF.asInstanceOf[Future[AnyRef]]
        val timeOut: Duration = ConfigService.getStringOpt(DoraCoreConfig.getConfig(),"dora.timeout") match {
          case Some(timeout) => timeout.toInt seconds
          case _=> 36000 seconds
        }
        val result =  Await.result(futureResult, timeOut)
        Right(result)
      }catch {
        case e: Throwable => println(e)
          Left(e)
      }
    }
  }

  def callProcessResult(processCallMsg: ProcessCallMsg)(implicit executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global): Future[ProcessResult] = {
    Future{callProcessResultSync(processCallMsg)}(executor)
  }

  def callProcessFutureResult(processCallMsg: ProcessCallMsg)(implicit executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global): Future[ProcessResult] = {
    Future{callProcessResultFutureSync(processCallMsg)}(executor)
  }

  private def callProcessResultSync(processCallMsg: ProcessCallMsg): ProcessResult = {
    callProcess(processCallMsg) match {
      case Right(x) =>
       ProcessResult(JobStatus.Finished,x)
      case Left(y) => ProcessResult(JobStatus.Failed,y)
    }
  }

  private def callProcessResultFutureSync(processCallMsg: ProcessCallMsg): ProcessResult = {
    callProcessAwaitFuture(processCallMsg) match {
      case Right(x) =>
        ProcessResult(JobStatus.Finished,x)
      case Left(y) => ProcessResult(JobStatus.Failed,y)
    }
  }

  case class ProcessCallMsg(clazzName: String, methodName: String, paras: Array[AnyRef])
  case class ProcessResult(jobStatus: JobStatus, result: Any)
}
