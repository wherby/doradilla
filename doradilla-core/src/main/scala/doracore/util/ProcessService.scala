package doracore.util

import akka.event.slf4j.Logger
import doracore.core.msg.Job.JobStatus.JobStatus


/**
  * For doradilla.util in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/22
  */
object ProcessService extends GetProcessFutureResult with GetProcessResult {
  var classLoaderOpt: Option[ClassLoader] = None

  def noImplementNameToClassOpt(className: String, classLoaderOpt: Option[ClassLoader]): Option[Class[_]] = {
    Logger.apply(this.getClass.toString).error("Not implement the name to class function")
    None
  }

  var nameToClassOpt: (String, Option[ClassLoader]) => Option[Class[_]] = noImplementNameToClassOpt

  def getProcessMethod(processCallMsg: ProcessCallMsg): ProcessCallMsg => Either[AnyRef, AnyRef] = {
    processCallMsg.instOpt match {
      case Some(instance) => callMethodForObject
      case _ => callProcess
    }
  }

  def callProcess(processCallMsg: ProcessCallMsg) = {
    //Call reflection will takes time
    // get runtime universe
    val ru = scala.reflect.runtime.universe
    // get runtime mirror
    val rm = ru.runtimeMirror(getClass.getClassLoader)
    //val instanceMirror = rm.reflectClass(Class.)
    try {
      lazy val aOpt = nameToClassOpt(processCallMsg.clazzName, classLoaderOpt)
      /*
        classLoaderOpt match {
        case Some(classloader) if processCallMsg.clazzName.indexOf("Processor")>=0 =>
          Some(Class.forName(processCallMsg.clazzName,false,classloader))
        case _ if processCallMsg.clazzName.indexOf("Processor" )>=0 =>
          Some(Class.forName(processCallMsg.clazzName))
        case _=>None
      }
      */
      aOpt match {
        case Some(a) =>
          val instance = a.newInstance()
          val processCallMsgNew = processCallMsg.copy(instOpt = Some(instance))
          callMethodForObject(processCallMsgNew)
        case _ =>
          Left("Class is not found.")
      }

    } catch {
      case e: Throwable =>
        Logger.apply(this.getClass.getName).error(e.getMessage)
        Left(e)
    }
  }


  def callMethodForObject(processCallMsg: ProcessCallMsg): Either[Throwable, AnyRef] = {
    val instance = processCallMsg.instOpt.get
    val methodOpt = instance.getClass.getMethods.filter(method => method.getName == processCallMsg.methodName).headOption
    methodOpt match {
      case Some(method) =>
        Right(method.invoke(instance, processCallMsg.paras: _*))
      case _ => Left(new Throwable("No such method"))
    }
  }

  case class ProcessCallMsg(clazzName: String, methodName: String, paras: Array[AnyRef], instOpt: Option[Any] = None)

  case class ProcessResult(jobStatus: JobStatus, result: Any)

}
