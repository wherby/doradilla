package doracore.tool.job.worker

import java.util.concurrent.Executors

import akka.actor.Actor
import doracore.vars.ConstVars

import scala.concurrent.ExecutionContext

trait BlockIODispatcher {
  this:Actor =>
  def GetBlockIODispatcher = {
    context.system.dispatchers.hasDispatcher(ConstVars.blockDispatcherName) match {
      case true => context.system.dispatchers.lookup(ConstVars.blockDispatcherName)
      case _ => ExecutionContext.fromExecutor(Executors.newFixedThreadPool(10))
    }
  }
}
