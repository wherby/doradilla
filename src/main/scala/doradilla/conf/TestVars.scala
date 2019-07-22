package doradilla.conf

import doracore.core.msg.Job.JobMsg
import doracore.tool.job.command.CommandTranActor.CommandRequest
import doracore.util.ProcessService.ProcessCallMsg
import play.api.libs.json.Json

/**
  * For io.github.wherby.doradilla.conf in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/5/18
  */
object TestVars {
  val commandWin = List("cmd.exe", "/c", "ping 127.0.0.1 -n 1")
  val commandLinux = List("bash", " -c", " ping 127.0.0.1 -c 1")
  val osString = System.getProperty("os.name")
  val command = osString.toLowerCase() match {
    case osStr if osStr.startsWith("win") => commandWin
    case _ => commandLinux
  }
  val commandRequest = CommandRequest(command)
  val commandJob = JobMsg("SimpleCommand", Json.toJson(commandRequest).toString())

  //process io.github.wherby.doradilla.test
  val paras = Array(command.asInstanceOf[AnyRef])
  val processCallMsgTest = ProcessCallMsg("doracore.util.CommandService", "runCommandSync", paras)
  val processJob = JobMsg("SimpleProcess", processCallMsgTest)
}
