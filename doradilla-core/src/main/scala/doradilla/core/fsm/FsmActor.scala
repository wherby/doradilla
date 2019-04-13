package doradilla.core.fsm

import akka.actor.{ActorLogging, ActorRef, FSM, PoisonPill}
import doradilla.base.BaseActor
import doradilla.base.query.QueryTrait.{ChildInfo, QueryChild}
import doradilla.core.fsm.FsmActor._
import doradilla.core.msg.Job._
import doradilla.core.msg.TranslationMSG.TranslatedTask
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
  var childActorOpt :Option[ActorRef] = None

  def hundleRequestList(requestList: RequestList)={
    if(requestList.requests.length >0){
      requestList.requests.map{
        request => request.tranActor ! request
          request.replyTo ! JobStatus.Scheduled
      }
    }
  }

  def endChildActor() ={
    childActorOpt.map{
      childActor => childActor ! PoisonPill
    }
    childActorOpt = None
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
    case Event(jobEnd: JobEnd, task: Task) =>
      val remainTask = task.requestList.requests.filter(request => jobEnd.requestMsg.taskMsg != request.taskMsg)
      if(remainTask.length >0){
        stay() using(Task(RequestList(remainTask)))
      }else{
        driverActor ! FetchJob()
        endChildActor()
        goto(Idle) using(Uninitialized)
      }
    case Event(workerInfo: WorkerInfo,_) =>
      childActorOpt = DeployService.tryToInstanceDeployActor(workerInfo,context)
      if(childActorOpt !=None && workerInfo.replyTo != None){
        workerInfo.replyTo.get ! TranslatedActor(childActorOpt.get)
      }
      stay()
    case Event(translatedTask: TranslatedTask,_)=>{
      childActorOpt.map{
        childActor=> childActor ! translatedTask.task
      }
      stay()
    }
  }


  whenUnhandled{
    case Event(queryChild: QueryChild,_) => val childInfo = ChildInfo(context.self.path.toString,getChildren(),System.currentTimeMillis()/1000)
      queryChild.actorRef ! childInfo
      this.context.children.map{ child=>
        child ! queryChild
      }
      stay()
    case Event(QueryState(),data)=>
      sender() !data
      log.info(s"QueryState: $data" )
      stay
    case Event(e,_)=>
      log.warning(s"Unhandle $e from $sender() in $stateName with $stateData")
      stay()
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
