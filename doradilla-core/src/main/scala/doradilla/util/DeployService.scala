package doradilla.util

import akka.actor.{ActorContext, ActorRef, Props}
import akka.event.slf4j.Logger
import doradilla.msg.Job.WorkerInfo

/**
  * For doradilla.util in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/30
  */
object DeployService {
  def tryToInstanceDeployActor(workerInfo: WorkerInfo, context:ActorContext ): Option[ActorRef] = {
    try {
      val clazz = Class.forName(workerInfo.actorName)
      val actorRef= workerInfo.config match {
        case Some(conf) =>   context.actorOf(Props(clazz,conf))
        case _=>context.actorOf(Props(clazz))
      }
      Some(actorRef)
    } catch {
      case ex: Throwable => Logger.apply(this.getClass.toString).error("Actor Failed " + ex.getMessage() + " " + ex.getStackTrace)
        None
    }
  }
}
