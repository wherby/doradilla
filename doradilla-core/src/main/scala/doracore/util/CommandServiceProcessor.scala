package doracore.util

import java.io.{ByteArrayOutputStream, PrintWriter}
import play.api.libs.json.Json
import scala.sys.process._
import scala.concurrent.{ExecutionContext, Future}

/**
  * For doradilla.util in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/7
  */
object CommandServiceProcessor {
  implicit val ExecuteResultFormat = Json.format[ExecuteResult]

  case class ExecuteResult(exitValue: Int, stdout: String, stderr: String)

  lazy val osString = System.getProperty("os.name")

  def runCommandProcess(cmdWin: List[String], cmdLinux: List[String])(implicit executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global): Future[ExecuteResult] = {
    val cmdProcess = osString.toLowerCase() match {
      case osStr if osStr.startsWith("win") => "cmd.exe" :: "/c" :: cmdWin
      case _ => "bash" :: "-c" :: cmdLinux
    }
    runCommand(cmdProcess)
  }

  def runCommand(cmdProcess: List[String])(implicit executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global): Future[ExecuteResult] = {
    Future(runCommandSync(cmdProcess))(executor)
  }

  def runCommandSync(cmdProcess: List[String]): ExecuteResult = {
    val stdoutStream = new ByteArrayOutputStream
    val stderrStream = new ByteArrayOutputStream
    val stdoutWriter = new PrintWriter(stdoutStream)
    val stderrWriter = new PrintWriter(stderrStream)
    val exitValue = cmdProcess.!(ProcessLogger(stdoutWriter.println, stderrWriter.println))
    stdoutWriter.close()
    stderrWriter.close()
    ExecuteResult(exitValue, stdoutStream.toString, stderrStream.toString)
  }
}

class CommandServiceProcessor {

}
