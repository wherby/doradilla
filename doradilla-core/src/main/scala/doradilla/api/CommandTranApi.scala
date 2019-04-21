package doradilla.api

import doradilla.tool.job.command.CommandTranActor

/**
  * For doradilla.api in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/13
  */
trait CommandTranApi {
  this: SystemApi =>
  val translatedActor = actorSystem.actorOf(CommandTranActor.commandTranProps, "CommandTran")
}
