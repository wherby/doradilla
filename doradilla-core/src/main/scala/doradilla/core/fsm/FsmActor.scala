package doradilla.core.fsm

import akka.actor.{ActorLogging, ActorRef, FSM}
import doradilla.base.BaseActor
import doradilla.core.fsm.FsmActor._
import doradilla.core.msg.Job._
import doradilla.core.queue.QueueActor
import doradilla.core.queue.QueueActor.RequestList
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
          request.replyTo ! JobStatus.Scheduled
      }
    }
  }

  when(Idle, stateTimeout = 1 second){
    case Event(requestList: RequestList,Uninitialized) =>{
      hundleRequestList(requestList)
      goto(Active) using(Task(requestList))
    }
    case Event(requestItem: JobRequest ,Uninitialized) =>
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


  when(Active) {
    case Event(endRequest: JobEnd, task: Task) =>
      val remainTask = task.requestList.requests.filter(request => endRequest.requestMsg.taskMsg != request.taskMsg)
      if(remainTask.length >0){
        stay() using(Task(RequestList(remainTask)))
      }else{
        driverActor ! FetchJob()
        childActor = None
        goto(Idle) using(Uninitialized)
      }
    case Event(workerInfo: WorkerInfo,_) =>
      childActor = DeployService.tryToInstanceDeployActor(workerInfo,context)
      if(childActor !=None && workerInfo.replyTo != None){
        workerInfo.replyTo.get ! TranslatedActor(childActor.get)
      }
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

  //Query fsm status
  case class QueryState()
  //Set driver ref in fsm
  case class SetDriver(ref:ActorRef)
  // Trigger Driver to fetch Job
  case class FetchJob()
  // SendBack child Actor to TranslationActor
  case class TranslatedActor(child: ActorRef)
}
