package doradilla.tool.control

import akka.actor.ActorRef
import doradilla.base.BaseActor
import doradilla.tool.control.ControlActor.ControlMsg

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * For doradilla.tool.control in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/8
  */
class ControlActor extends BaseActor{
  def handleControlMsg(controlMsg: ControlMsg, sender:ActorRef) ={
    log.info(s"Handle control msg: $controlMsg")
    val actorSelect = context.actorSelection(controlMsg.actorPath)
    actorSelect.resolveOne( 100 milliseconds).map{
      actor =>
        log.info(actor.toString())
        log.info(s"Handled control msg: $controlMsg")
        actor.tell(controlMsg.controlMsg,sender)
    }
  }

  override def receive: Receive = {
    case controlMsg: ControlMsg => handleControlMsg(controlMsg,sender())
  }
}

object ControlActor{
  case class ControlMsg(actorPath: String, controlMsg: Any)
}
