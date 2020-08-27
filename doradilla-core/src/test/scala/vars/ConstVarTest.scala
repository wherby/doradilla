package vars

import doracore.core.msg.Job.JobMsg
import doracore.tool.job.command.CommandTranActor.CommandRequest
import doracore.util.ProcessService.ProcessCallMsg
import jobs.fib.FibnacciTranActor.FibRequest
import play.api.libs.json.Json

import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * For `var` in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/30
  */
object ConstVarTest {
  val fibTask = JobMsg("fibreq", Json.toJson(FibRequest(10)).toString)

  def fibTaskN(n: Int) = JobMsg("fibreq", Json.toJson(FibRequest(n)).toString)

  val cmdWin = List("ping 127.0.0.1 -n 1")
  val cmdLinux = List("ping 127.0.0.1 -c 1")
  val commandWin = List("cmd.exe", "/c", "ping 127.0.0.1 -n 1")
  val commandLinux = List("bash", " -c", " ping 127.0.0.1 -c 1")
  val timeout100m = 100 milliseconds
  val timeout10S = 10 second

  val osString = System.getProperty("os.name")
  val command = osString.toLowerCase() match {
    case osStr if osStr.startsWith("win") => ConstVarTest.commandWin
    case _ => ConstVarTest.commandLinux
  }
  val commandRequest = CommandRequest(command)
  val commandJob = JobMsg("SimpleCommand", Json.toJson(commandRequest).toString())

//process io.github.wherby.doradilla.test
  val paras = Array(ConstVarTest.command.asInstanceOf[AnyRef])
  val processCallMsgTest = ProcessCallMsg("doracore.util.CommandServiceProcessor","runCommandSync",paras)
  val processJob = JobMsg("SimpleProcess",processCallMsgTest)
}
