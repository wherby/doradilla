package doracore.tool.control

import akka.actor.{ActorRef, Props}
import doracore.base.BaseActor
import doracore.tool.control.ControlActor.ControlMsg
import doracore.vars.ConstVars
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * For doradilla.tool.control in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/8
  */
class ControlActor extends BaseActor {
  def handleControlMsg(controlMsg: ControlMsg, sender: ActorRef) = {
    val actorSelect = context.actorSelection(controlMsg.actorPath)
    actorSelect.resolveOne(ConstVars.timeout1S).map {
      actor =>
        log.info(actor.toString())
        log.info(s"Handled control msg: $controlMsg")
        actor.tell(controlMsg.controlMsg, sender)
    }
  }

  override def receive: Receive = {
    case controlMsg: ControlMsg => handleControlMsg(controlMsg, sender())
  }
}

object ControlActor {
  val controlActorProps = Props(new ControlActor())

  case class ControlMsg(actorPath: String, controlMsg: Any)

}
