package doracore.core.driver

import akka.actor.{ActorRef, Props}
import doracore.base.BaseActor
import akka.event.LoggingReceive
import com.datastax.driver.core.utils.UUIDs
import doracore.core.driver.DriverActor.{FetchQueue, ProxyActorMsg}
import doracore.core.fsm.FsmActor
import doracore.core.fsm.FsmActor.{FetchJob, RegistToDriver, SetDriver}
import doracore.core.msg.Job.{JobMeta, JobRequest}
import doracore.core.proxy.ProxyActor
import doracore.core.queue.QueueActor
import doracore.core.queue.QueueActor.{FetchTask, RequestListResponse}
import doracore.util.CNaming

/**
  * For doradilla.driver in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/30
  */
class DriverActor(queue: Option[ActorRef] = None, setDefaultFsmActor: Option[Boolean] = Some(true)) extends BaseActor {
  val queueActor = queue match {
    case Some(queue) => queue
    case _ =>
      context.actorOf(QueueActor.queueActorProps, CNaming.timebasedName( "queueActor"))
  }

  if(setDefaultFsmActor == Some(true)){
    val fsmActor: ActorRef = context.actorOf(DriverActor.fsmProps, CNaming.timebasedName( "fsmActor"))
    fsmActor ! SetDriver(self)
  }

  def createProxy(proxyName: String): ActorRef = {
    context.actorOf(ProxyActor.proxyProps(queueActor), proxyName)
  }

  def handleRequest(jobRequestOrg: JobRequest) = {
    val jobRequest = jobRequestOrg.jobMetaOpt match {
      case Some(_) => jobRequestOrg
      case _=> jobRequestOrg.copy(jobMetaOpt = Some(JobMeta(UUIDs.timeBased().toString)))
    }
    val proxyActor = createProxy(CNaming.timebasedName( jobRequest.taskMsg.operation ))
    log.info(s"{${jobRequest.jobMetaOpt}} is handled by proxy $proxyActor")
    proxyActor ! jobRequest
    sender() ! ProxyActorMsg(proxyActor)
  }

  def hundleFetchJob() = {
    queueActor ! FetchTask(1,sender())
  }

  def hundleRequestListResponse(requestListResponse: RequestListResponse) = {
    if (requestListResponse.requestList.requests.length > 0) {
      requestListResponse.requestActor ! requestListResponse.requestList
    }
  }

  def handleRegister(registToDriver: RegistToDriver) ={
    registToDriver.actorRef ! SetDriver(self)
  }

  def handleFetchQueue()={
    sender() ! queueActor
  }


  override def receive: Receive = LoggingReceive {
    case jobRequest: JobRequest => handleRequest(jobRequest)
    case fetchJob: FetchJob => hundleFetchJob()
    case requestListResponse: RequestListResponse =>hundleRequestListResponse(requestListResponse)
    case registToDriver: RegistToDriver => handleRegister(registToDriver)
    case _: FetchQueue => handleFetchQueue()
  }
}

object DriverActor {
  def driverActorProps(queue: Option[ActorRef] = None) = {
    Props(new DriverActor(queue))
  }

  def driverActorPropsWithoutFSM(queue: Option[ActorRef] = None) = {
    Props(new DriverActor(queue,None))
  }

  def fsmProps: Props = Props(new FsmActor)

  case class ProxyActorMsg(proxyActor: ActorRef)

  case class FetchQueue()

}
