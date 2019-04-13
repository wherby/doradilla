package vars

import doradilla.core.msg.Job.JobMsg
import doradilla.tool.job.command.CommandTranActor.CommandRequest
import jobs.fib.FibnacciTranActor.FibRequest
import jobs.process.ProcessTranActor.ProcessRequest
import play.api.libs.json.Json

import scala.concurrent.duration._

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
  val processRequest = ProcessRequest(cmdWin, cmdLinux)
  val processJob = JobMsg("SimpleProcess", Json.toJson(processRequest).toString)
  val timeout100m = 100 milliseconds
  val timeout10S = 100 second

  val osString = System.getProperty("os.name")
  val command = osString.toLowerCase() match {
    case osStr if osStr.startsWith("win") => ConstVarTest.commandWin
    case _ => ConstVarTest.commandLinux
  }
  val commandRequest = CommandRequest(command)
  val commandJob = JobMsg("SimpleCommand", Json.toJson(commandRequest).toString())
}
