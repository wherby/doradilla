package jobs.process

import akka.actor.Props
import doradilla.core.msg.WorkerMsg.TickMsg
import doradilla.tool.job.worker.WorkerActor
import doradilla.util.ProcessService
import jobs.process.ProcessTranActor.SimpleProcessInit


/**
  * For jobs.process in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/7
  */
class ProcessWorkerActor extends WorkerActor{

  def handleProcessInit(simpleProcessInit: SimpleProcessInit)={
    futureResultOpt = Some(ProcessService.runProcess(simpleProcessInit.processRequest.cmdWin,
      simpleProcessInit.processRequest.cmdLinux, dispatcherToUse))
    replyToOpt = Some(simpleProcessInit.replyTo)
    cancelableSchedulerOpt = Some(context.system.scheduler.schedule(tickTime,tickTime,this.self,TickMsg()))
  }

  override def receive: Receive =  super.receive orElse {
    case simpleProcessInit: SimpleProcessInit => handleProcessInit(simpleProcessInit)
  }
}

object ProcessWorkerActor{
  val processWorkerActorProps = Props(new ProcessWorkerActor())
}

