package doracore.util

import akka.event.slf4j.Logger

object AppDebugger {
  def log(str:String, domain:Option[String])={
    val logger= Logger.apply(getClass.getName)
    logger.info(s"$domain >>>>")
    logger.info(str)
    logger.info(s"$domain <<<<")
  }
}
