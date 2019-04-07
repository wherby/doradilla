package doradilla.util

import java.io.{ByteArrayOutputStream, PrintWriter}

import play.api.libs.json.Json

import scala.sys.process._
import scala.concurrent.{ExecutionContext, Future}

/**
  * For doradilla.util in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/7
  */
object ProcessService {
  implicit val ExecuteResultFormat = Json.format[ExecuteResult]
  case class ExecuteResult(exitValue:Int, stdout:String, stderr:String)
  lazy val osString = System.getProperty("os.name")
  def runProcess(cmd:List[String],executor : ExecutionContext =scala.concurrent.ExecutionContext.Implicits.global) :Future[ExecuteResult]={
    val cmdProcess =osString.toLowerCase() match {
      case osStr if osStr.startsWith("win") => "cmd.exe" :: "/c" :: cmd
      case _=> "bash" :: "-c" :: cmd
    }
    Future(try{
      val stdoutStream = new ByteArrayOutputStream
      val stderrStream = new ByteArrayOutputStream
      val stdoutWriter = new PrintWriter(stdoutStream)
      val stderrWriter = new PrintWriter(stderrStream)
      val exitValue = cmdProcess.!(ProcessLogger(stdoutWriter.println, stderrWriter.println))
      stdoutWriter.close()
      stderrWriter.close()
      ExecuteResult(exitValue, stdoutStream.toString, stderrStream.toString)
    }catch {
      case e:Exception =>ExecuteResult(-1, "",s"Exception is throwing: ${e.getCause.getMessage}")
    })(executor)
  }
}
