package doracore.tool.job.worker


import akka.actor.Actor
import doracore.vars.ConstVars



trait BlockIODispatcher {
  this:Actor =>
  def GetBlockIODispatcher = {
    context.system.dispatchers.hasDispatcher(ConstVars.blockDispatcherName) match {
      case true => context.system.dispatchers.lookup(ConstVars.blockDispatcherName)
      case _ => context.system.dispatcher
    }
  }
}
