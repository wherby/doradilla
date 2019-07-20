package doracore.tool.job.process

import akka.actor.{ActorLogging, Props}
import doracore.core.msg.WorkerMsg.TickMsg
import doracore.tool.job.process.ProcessTranActor.SimpleProcessInit
import doracore.tool.job.worker.WorkerActor
import doracore.util.ProcessService

/**
  * For doradilla.tool.job.process in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/22
  */

class ProcessWorkerActor extends WorkerActor with ActorLogging{

  def handleSimpleProcessInit(simpleProcessInit: SimpleProcessInit) ={
    futureResultOpt = Some(ProcessService.callProcessResult(simpleProcessInit.processCallMsg))
    replyToOpt = Some(simpleProcessInit.replyTo)
    cancelableSchedulerOpt = Some(context.system.scheduler.schedule(tickTime, tickTime, this.self, TickMsg()))
  }

  override def receive: Receive = super.receive orElse{
    case simpleProcessInit: SimpleProcessInit => handleSimpleProcessInit(simpleProcessInit)
  }
}

object ProcessWorkerActor{
  val processTranActorProps = Props(new ProcessWorkerActor())
}
