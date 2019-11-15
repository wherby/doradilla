package doracore.api

import scala.concurrent.ExecutionContextExecutor

trait GetBlockIOExcutor {
  def getBlockDispatcher():ExecutionContextExecutor
}
