package doracore.util

import java.util.UUID
import akka.actor.{ActorContext, ActorRef, Props}
import akka.event.slf4j.Logger
import doracore.core.msg.Job.WorkerInfo

/**
  * For doradilla.util in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/30
  */
object DeployService {
  var classLoaderOpt: Option[ClassLoader] = None

  def tryToInstanceDeployActor(workerInfo: WorkerInfo, context: ActorContext): Option[ActorRef] = {
    try {
      val clazz = classLoaderOpt match {
        case Some(classLoader) => Class.forName(workerInfo.actorName, false,classLoader)
        case _=> Class.forName(workerInfo.actorName)
      }
      val actorName = clazz.getSimpleName + UUID.randomUUID().toString
      val actorRef = workerInfo.config match {
        case Some(conf) => context.actorOf(Props(clazz, conf), actorName)
        case _ => context.actorOf(Props(clazz), actorName)
      }
      Some(actorRef)
    } catch {
      case ex: Throwable => Logger.apply(this.getClass.toString).error("Actor Failed " + ex.getMessage() + " " + ex.getStackTrace)
        None
    }
  }
}
