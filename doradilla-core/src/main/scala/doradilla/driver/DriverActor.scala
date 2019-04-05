package doradilla.driver

import akka.actor.{ActorRef, Props}
import doradilla.base.BaseActor
import doradilla.fsm.FsmActor
import doradilla.fsm.FsmActor.{FetchJob, SetDriver}
import doradilla.msg.Job.JobRequest
import doradilla.proxy.ProxyActor
import doradilla.queue.QueueActor
import doradilla.queue.QueueActor.{FetchTask, RequestList}
import akka.event.LoggingReceive
/**
  * For doradilla.driver in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/30
  */
class DriverActor(queue: Option[ActorRef] = None) extends BaseActor{

  val queueActor = queue match {
    case Some(queue) => queue
    case _=>context.actorOf(DriverActor.queueProps)
  }
  val fsmActor : ActorRef = context.actorOf(DriverActor.fsmProps)
  fsmActor ! SetDriver(self)

  def createProxy():ActorRef={
    context.actorOf(DriverActor.proxyProps(queueActor))
  }
  def handleRequest(requestMsg: JobRequest)={
    val proxyActor = createProxy()
    proxyActor ! requestMsg
    sender()! proxyActor
  }

  def hundleFetchJob()={
    queueActor ! FetchTask(1)
  }

  def hundleRequestList(requestList: RequestList)={
    if(requestList.requests.length >0){
      fsmActor !requestList
    }
  }

  override def receive: Receive = LoggingReceive{
    case requestMsg: JobRequest => handleRequest(requestMsg)
    case fetchJob: FetchJob =>hundleFetchJob()
    case requestList: RequestList => hundleRequestList(requestList)
  }
}

object DriverActor{
  def proxyProps(queue:ActorRef):Props = Props(new ProxyActor(queue))
  def queueProps:Props = Props(new QueueActor)
  def fsmProps:Props = Props(new FsmActor)
}
