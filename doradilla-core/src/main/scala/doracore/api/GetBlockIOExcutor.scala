package doracore.api

import doracore.vars.ConstVars

import scala.concurrent.ExecutionContextExecutor

trait GetBlockIOExcutor {
  this :ActorSystemApi=>
  def getBlockDispatcher():ExecutionContextExecutor={
    val actorSystem= getActorSystem()
    actorSystem.dispatchers.hasDispatcher(ConstVars.blockDispatcherName) match {
      case true => actorSystem.dispatchers.lookup(ConstVars.blockDispatcherName)
      case _ => actorSystem.dispatcher
    }
  }
}
