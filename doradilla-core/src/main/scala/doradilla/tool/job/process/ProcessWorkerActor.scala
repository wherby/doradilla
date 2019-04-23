package doradilla.tool.job.process

import java.util.concurrent.Executors

import akka.actor.Props
import doradilla.core.msg.WorkerMsg.TickMsg
import doradilla.tool.job.process.ProcessTranActor.SimpleProcessInit
import doradilla.tool.job.worker.WorkerActor
import doradilla.util.ProcessService

import scala.concurrent.ExecutionContext

/**
  * For doradilla.tool.job.process in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/22
  */

class ProcessWorkerActor extends WorkerActor{
  implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(10))
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
