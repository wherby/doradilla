package doracore.tool.job.command

import akka.actor.Props
import doracore.core.msg.WorkerMsg.TickMsg
import doracore.tool.job.command.CommandTranActor.SimpleCommandInit
import doracore.tool.job.worker.WorkerActor
import doracore.util.CommandServiceProcessor

/**
  * For doradilla.tool.job.command in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/13
  */
class CommandWorkerActor extends WorkerActor {
  def handleSimpleCommandInit(simpleCommandInit: SimpleCommandInit):Unit = {
    futureResultOpt = Some(CommandServiceProcessor.runCommand(simpleCommandInit.commandRequest.command))
    replyToOpt = Some(simpleCommandInit.repleyTo)
    cancelableSchedulerOpt = Some(context.system.scheduler.schedule(tickTime, tickTime, this.self, TickMsg()))
  }

  override def receive: Receive = super.receive orElse {
    case simpleCommandInit: SimpleCommandInit => handleSimpleCommandInit(simpleCommandInit)
  }
}

object CommandWorkerActor {
  val commandWorkerProps = Props(new CommandWorkerActor())
}
