package doradilla.fsm

import akka.actor.{ActorLogging, FSM}
import doradilla.base.BaseActor
import doradilla.fsm.FsmActor._
import doradilla.msg.TaskMsg.RequestItem
import scala.concurrent.duration._

/**
  * For doradilla.fsm in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/24
  */
class FsmActor extends FSM[State,Data] with BaseActor with ActorLogging{
  startWith(Idle,Uninitialized)

  when(Idle){
    case Event(requestItem: RequestItem ,Uninitialized) =>
      requestItem.actorRef ! requestItem
      goto(Active) using(Task(requestItem))
  }

  onTransition{
    case Active->Idle  =>{
      stateData match {
        case Task(requestItem) => log.info( s"To process $requestItem")
        case Uninitialized =>
      }
    }
  }

  when(Active, stateTimeout = 1 second) {
    case Event( StateTimeout, ex) =>
      stay()
  }


  whenUnhandled{
    case Event(QueryState(),data)=>
      log.info(s"Not hundle : $data")
      sender() !data
      log.info(s"Send back: $data" )
      stay
  }

}


object FsmActor{
  sealed trait State
  case object Idle extends State
  case object Active extends State

  sealed  trait Data
  case object Uninitialized extends Data
  final  case class Task(requestItem: RequestItem )extends Data

  case class QueryState()
}
