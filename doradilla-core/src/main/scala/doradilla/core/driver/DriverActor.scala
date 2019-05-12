package doradilla.core.driver

import java.util.UUID

import akka.actor.{ActorRef, Props}
import doradilla.base.BaseActor
import akka.event.LoggingReceive
import doradilla.core.driver.DriverActor.ProxyActorMsg
import doradilla.core.fsm.FsmActor
import doradilla.core.fsm.FsmActor.{FetchJob, RegistToDriver, SetDriver}
import doradilla.core.msg.Job.JobRequest
import doradilla.core.proxy.ProxyActor
import doradilla.core.queue.QueueActor
import doradilla.core.queue.QueueActor.{FetchTask, RequestListResponse}

/**
  * For doradilla.driver in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/30
  */
class DriverActor(queue: Option[ActorRef] = None, setDefaultFsmActor: Option[Boolean] = Some(true)) extends BaseActor {
  val driverUUID = UUID.randomUUID().toString
  val queueActor = queue match {
    case Some(queue) => queue
    case _ =>
      context.actorOf(QueueActor.queueActorProps, "queueActor" + driverUUID)
  }

  if(setDefaultFsmActor == Some(true)){
    val fsmActor: ActorRef = context.actorOf(DriverActor.fsmProps, "fsmActor" + driverUUID)
    fsmActor ! SetDriver(self)
  }

  def createProxy(proxyName: String): ActorRef = {
    context.actorOf(ProxyActor.proxyProps(queueActor), proxyName)
  }

  def handleRequest(jobRequest: JobRequest) = {
    val proxyActor = createProxy(jobRequest.taskMsg.operation + UUID.randomUUID().toString)
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


  override def receive: Receive = LoggingReceive {
    case jobRequest: JobRequest => handleRequest(jobRequest)
    case fetchJob: FetchJob => hundleFetchJob()
    case requestListResponse: RequestListResponse =>hundleRequestListResponse(requestListResponse)
    case registToDriver: RegistToDriver => handleRegister(registToDriver)
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

}
