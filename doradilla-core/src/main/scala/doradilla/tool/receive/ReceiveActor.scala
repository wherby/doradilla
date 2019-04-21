package doradilla.tool.receive

import akka.actor.{ActorRef, PoisonPill, Props}
import doradilla.base.BaseActor
import doradilla.core.driver.DriverActor.ProxyActorMsg
import doradilla.core.msg.Job.JobResult
import doradilla.tool.receive.ReceiveActor.{FetchResult, ProxyControlMsg, StopProxy}

/**
  * For doradilla.tool.receive in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/13
  */
class ReceiveActor extends BaseActor{
  var retriverActorOpt: Option[ActorRef] = None
  var jobResultOpt: Option[JobResult] = None
  var proxyActorOpt:Option[ActorRef]= None

  def sendBackReuslt()= {
    retriverActorOpt.get ! jobResultOpt.get
  }

  def handleFetchMsg()={
    retriverActorOpt = Some(sender())
    jobResultOpt match {
      case Some(jobResult) => sendBackReuslt()
      case _=>
    }
  }


  def handleJobResult(jobResult: JobResult)={
    jobResultOpt = Some(jobResult)
    retriverActorOpt match {
      case Some(retriverActor) => sendBackReuslt()
      case _=>
    }
  }

  def handleProxyActorMsg(msg: ProxyActorMsg)={
    proxyActorOpt = Some(msg.proxyActor)
  }


  def handleProxyControlMsg(proxyControlMsg: ProxyControlMsg) ={
    proxyActorOpt.map{
      proxyActor => proxyActor ! proxyControlMsg.proxyControlMsg
    }
  }

  override def receive: Receive = {
    case msg:FetchResult => handleFetchMsg()
    case jobResult: JobResult => handleJobResult(jobResult)
    case proxyActorMsg: ProxyActorMsg => handleProxyActorMsg(proxyActorMsg)
    case proxyControlMsg: ProxyControlMsg => handleProxyControlMsg(proxyControlMsg)
  }
}

object ReceiveActor{
  val receiveActorProps = Props(new ReceiveActor())
  case class FetchResult()
  case class StopProxy()
  case class ProxyControlMsg(proxyControlMsg: Any)
}
