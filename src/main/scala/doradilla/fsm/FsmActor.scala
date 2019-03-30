package doradilla.fsm

import akka.actor.{ActorLogging, ActorRef, FSM}
import doradilla.base.BaseActor
import doradilla.fsm.FsmActor._
import doradilla.msg.TaskMsg._
import doradilla.queue.QueueActor
import doradilla.queue.QueueActor.{ RequestList}
import doradilla.util.DeployService

import scala.concurrent.duration._

/**
  * For doradilla.fsm in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/24
  */
class FsmActor extends FSM[State,Data] with BaseActor with ActorLogging{
  startWith(Idle,Uninitialized)
  var driverActor :ActorRef = null
  var childActor :Option[ActorRef] = None

  def hundleRequestList(requestList: RequestList)={
    if(requestList.requests.length >0){
      requestList.requests.map{
        request => request.tranActor ! request
          request.replyTo ! TaskStatus.Scheduled
      }
    }
  }

  when(Idle, stateTimeout = 1 second){
    case Event(requestItem: RequestMsg ,Uninitialized) =>
      val requestList = QueueActor.RequestList(Seq(requestItem))
      hundleRequestList(requestList)
      goto(Active) using(Task(requestList))
    case Event(setDriver: SetDriver,Uninitialized) =>{
      driverActor = setDriver.ref
      stay()
    }
    case Event(StateTimeout, Uninitialized) =>{
      if(driverActor != null){
        driverActor ! FetchJob()
      }
      stay()
    }
  }

  onTransition{
    case Active->Idle  =>{
      childActor = None
    }
  }

  when(Active) {
    case Event(endRequest: EndRequest, task: Task) =>
      val remainTask = task.requestList.requests.filter(request => endRequest.requestMsg != request)
      if(remainTask.length >0){
        stay() using(Task(RequestList(remainTask)))
      }else{
        driverActor ! FetchJob()
        goto(Idle) using(Uninitialized)
      }
    case Event(workerInfo: WorkerInfo,_) =>
      childActor = DeployService.tryToInstanceDeployActor(workerInfo,context)
      stay()
    case Event(translatedTask: TranslatedTask,_)=>{
      if(childActor != None){
        childActor.get ! translatedTask.task
      }
      stay()
    }
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
  final  case class Task(requestList: RequestList)extends Data

  case class QueryState()
  case class SetDriver(ref:ActorRef)
  case class FetchJob()
}
