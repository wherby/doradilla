package doracore.core.fsm

import akka.actor.{ActorLogging, ActorRef, Cancellable, FSM, PoisonPill, Props}
import akka.event.slf4j.Logger
import doracore.base.BaseActor
import doracore.base.query.QueryTrait.{ChildInfo, QueryChild}
import doracore.core.fsm.FsmActor._
import doracore.core.msg.Job._
import doracore.core.msg.JobControlMsg.ResetFsm
import doracore.core.msg.TranslationMsg.TranslatedTask
import doracore.core.queue.QueueActor
import doracore.core.queue.QueueActor.RequestList
import doracore.util.{ConfigService, DeployService}

import scala.concurrent.duration._

/**
  * For doradilla.fsm in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/24
  */
class FsmActor extends FSM[State, Data] with BaseActor with ActorLogging {
  startWith(Idle, Uninitialized)
  var driverActor: ActorRef = null
  var childActorOpt: Option[ActorRef] = None
  var jobMetaOpt: Option[JobMeta] = None
  var cancelableSchedulerOpt: Option[Cancellable] = None
  val ex =scala.concurrent.ExecutionContext.Implicits.global
  lazy val timeoutConf:Option[Int] =  ConfigService.getIntOpt(context.system.settings.config, "doradilla.fsm.timeout")

  def hundleRequestList(requestList: RequestList) = {
    if (requestList.requests.length > 0) {
      setTimeOutCheck()
      requestList.requests.map {
        request =>
          jobMetaOpt =request.jobMetaOpt
          log.info(s"{${request.jobMetaOpt}} is started in fsm worker, and will be handled by {${request.tranActor}}")
          log.debug(s"${request.taskMsg} is handled in FSM actor, the task will be start soon")
          request.tranActor ! request
          request.replyTo ! JobStatus.Scheduled
      }
    }
  }

  def endChildActor() = {
    log.info(s"$jobMetaOpt is end")
    jobMetaOpt = None
    childActorOpt.map {
      childActor => childActor ! PoisonPill
    }
    childActorOpt = None
  }

  def setTimeOutCheck()={
    timeoutConf.map{
      timeout => val delay:FiniteDuration = timeout.seconds
        if(cancelableSchedulerOpt != None){
          log.info("There are existed cancelable scheduler which will be clean...")
          cancelableSchedulerOpt.map{
            cancelableScheduler => cancelableScheduler.cancel()
          }
          cancelableSchedulerOpt = None
        }
        log.error(s"set timeout ot $timeout with $delay")
        cancelableSchedulerOpt = Some(context.system.scheduler.scheduleOnce(delay,self,FSMTimeout("FSMtimeout"))(ex))
    }
  }

  when(Idle, stateTimeout = 1 second) {
    case Event(requestList: RequestList, Uninitialized) => {
      hundleRequestList(requestList)
      goto(Active) using (Task(requestList))
    }
    case Event(requestItem: JobRequest, Uninitialized) =>
      val requestList = QueueActor.RequestList(Seq(requestItem))
      hundleRequestList(requestList)
      goto(Active) using (Task(requestList))
    case Event(setDriver: SetDriver, Uninitialized) => {
      driverActor = setDriver.ref
      stay()
    }
    case Event(StateTimeout, Uninitialized) => {
      if (driverActor != null) {
        driverActor ! FetchJob()
      }
      stay()
    }
  }

  onTransition {
    case Active -> Idle =>
      cancelableSchedulerOpt.map({
        cancelableScheduler => cancelableScheduler.cancel()
      })
      cancelableSchedulerOpt = None
      endChildActor()
      driverActor ! FetchJob()
  }

  when(Active) {
    case Event(jobEnd: JobEnd, task: Task) =>
        goto(Idle) using (Uninitialized)
    case Event(workerInfo: WorkerInfo, _) =>
      childActorOpt = DeployService.tryToInstanceDeployActor(workerInfo, context)
      if (childActorOpt != None && workerInfo.replyTo != None) {
        workerInfo.replyTo.get ! TranslatedActor(childActorOpt.get)
      }
      stay()
    case Event(translatedTask: TranslatedTask, _) => {
      childActorOpt.map {
        childActor => childActor ! translatedTask.task
      }
      stay()
    }
  }


  whenUnhandled {
    case Event(fsmtim: FSMTimeout,_) =>
      log.error("FSM Timeout and reset to uninitialized state..")
      log.error(s"$jobMetaOpt will need be cleaned by user.")
      goto(Idle) using (Uninitialized)
    case Event(resetFsm: ResetFsm, _)=>
      log.info("Reset fsm actor..")
      goto(Idle) using (Uninitialized)
    case Event(queryChild: QueryChild, _) => val childInfo = ChildInfo(context.self.path.toString, getChildren(), System.currentTimeMillis() / 1000)
      queryChild.actorRef ! childInfo
      stay()
    case Event(QueryState(), data) =>
      sender() ! data
      log.info(s"QueryState: $data")
      stay
    case Event(e, _) =>
      log.warning(s"Unhandle $e from $sender() in $stateName with $stateData")
      stay()
  }

}


object FsmActor {
  val fsmActorProps = Props(new FsmActor())

  sealed trait State

  case object Idle extends State

  case object Active extends State

  sealed trait Data

  case object Uninitialized extends Data

  final case class Task(requestList: RequestList) extends Data

  //Query fsm status
  case class QueryState()

  case class RegistToDriver(actorRef: ActorRef)
  //Set driver ref in fsm
  case class SetDriver(ref: ActorRef)

  // Trigger Driver to fetch Job
  case class FetchJob()

  // SendBack child Actor to TranslationActor
  case class TranslatedActor(child: ActorRef)

  case class FSMTimeout(info :String)


}
