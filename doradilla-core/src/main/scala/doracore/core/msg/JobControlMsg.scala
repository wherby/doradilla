package doracore.core.msg

import akka.actor.ActorRef

/**
  * For doradilla.core.msg in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/21
  */
object JobControlMsg {

  sealed trait ControlMsg

  final case class ResetJob(jobActor:ActorRef) extends ControlMsg

  final case class ResetFsm() extends ControlMsg

}
