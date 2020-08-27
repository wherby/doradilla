package doracore.core.driver

import akka.actor.{ActorRef, Props}
import doracore.base.BaseActor
import akka.event.LoggingReceive
import doracore.core.driver.DriverActor._
import doracore.core.fsm.FsmActor
import doracore.core.fsm.FsmActor.{FetchJob, RegistToDriver, SetDriver}
import doracore.core.msg.Job.{JobMeta, JobRequest}
import doracore.core.proxy.ProxyActor
import doracore.core.queue.QueueActor
import doracore.core.queue.QueueActor.{FetchTask, RequestListResponse}
import doracore.util.{CNaming, MyUUID}

/**
  * For doradilla.driver in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/30
  */
class DriverActor(queue: Option[ActorRef] = None, setDefaultFsmActor: Option[Boolean] = Some(true)) extends BaseActor {
  var fsmToBeDecrease = 0
  val queueActor = queue match {
    case Some(queue) => queue
    case _ =>
      context.actorOf(QueueActor.queueActorProps, CNaming.timebasedName("queueActor"))
  }

  if (setDefaultFsmActor == Some(true)) {
    createOneFSMActor()
  }

  private def createOneFSMActor() = {
    val fsmActor: ActorRef = context.actorOf(DriverActor.fsmProps, CNaming.timebasedName("fsmActor"))
    fsmActor ! SetDriver(self)
  }

  def createProxy(proxyName: String): ActorRef = {
    context.actorOf(ProxyActor.proxyProps(queueActor), proxyName)
  }

  def handleRequest(jobRequestOrg: JobRequest) = {
    val jobRequest = jobRequestOrg.jobMetaOpt match {
      case Some(_) => jobRequestOrg
      case _ => jobRequestOrg.copy(jobMetaOpt = Some(JobMeta(MyUUID.getUUIDString())))
    }
    val proxyActor = createProxy(CNaming.timebasedName(jobRequest.taskMsg.operation))
    log.info(s"{${jobRequest.jobMetaOpt}} is handled by proxy $proxyActor")
    proxyActor ! jobRequest
    sender() ! ProxyActorMsg(proxyActor)
  }

  def hundleFetchJob() = {
    if(fsmToBeDecrease > 0){
      fsmToBeDecrease = fsmToBeDecrease -1
      sender() ! FSMDecrease(1)
    }else{
      queueActor ! FetchTask(1, sender())
    }
  }

  def hundleRequestListResponse(requestListResponse: RequestListResponse) = {
    if (requestListResponse.requestList.requests.length > 0) {
      requestListResponse.requestActor ! requestListResponse.requestList
    }
  }

  def handleRegister(registToDriver: RegistToDriver) = {
    registToDriver.actorRef ! SetDriver(self)
  }

  def handleFetchQueue() = {
    sender() ! queueActor
  }

  def handleFSMControl(fsmControl: FSMControl) = {
    fsmControl match {
      case FSMIncrease(num) if (num > 0  && num < 1000)=> for (_ <- 1 to num) {
        log.info("Increase FSMActor.")
        createOneFSMActor()
      }
      case FSMDecrease(num) => fsmToBeDecrease = fsmToBeDecrease + num
    }
  }


  override def receive: Receive = LoggingReceive {
    case jobRequest: JobRequest => handleRequest(jobRequest)
    case fetchJob: FetchJob => hundleFetchJob()
    case requestListResponse: RequestListResponse => hundleRequestListResponse(requestListResponse)
    case registToDriver: RegistToDriver => handleRegister(registToDriver)
    case _: FetchQueue => handleFetchQueue()
    case fsmControl: FSMControl => handleFSMControl(fsmControl)
  }
}

object DriverActor {
  def driverActorProps(queue: Option[ActorRef] = None) = {
    Props(new DriverActor(queue))
  }

  def driverActorPropsWithoutFSM(queue: Option[ActorRef] = None) = {
    Props(new DriverActor(queue, None))
  }

  def fsmProps: Props = Props(new FsmActor)

  case class ProxyActorMsg(proxyActor: ActorRef)

  case class FetchQueue()

  sealed trait FSMControl

  case class FSMIncrease(increase: Int) extends FSMControl

  case class FSMDecrease(decrease: Int) extends FSMControl

}
