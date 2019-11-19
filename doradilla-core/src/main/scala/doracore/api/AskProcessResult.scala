package doracore.api

import akka.actor.{ActorRef, PoisonPill}
import akka.event.slf4j.Logger
import akka.util.Timeout
import doracore.core.msg.Job.{JobRequest, JobResult, JobStatus}
import doracore.tool.receive.ReceiveActor.{FetchResult, ProxyControlMsg}

import scala.concurrent.{Await, Future}
import akka.pattern.ask
import doracore.util.ProcessService.ProcessResult

trait AskProcessResult {
  this:GetBlockIOExcutor =>
  def getProcessCommandFutureResult(jobRequest: JobRequest, driver:ActorRef, receiveActor: ActorRef, timeout: Timeout ): Future[JobResult] = {
    driver.tell(jobRequest, receiveActor)
    var result = JobResult(JobStatus.Unknown, "Unkonwn").asInstanceOf[Any]

    def getResult = {
      implicit val timeoutValue: Timeout = timeout
      try {
        result = Await.result((receiveActor ? FetchResult()), timeout.duration)
      } catch {
        case ex: Throwable =>
          val tName = Thread.currentThread.getName
          Logger.apply(this.getClass.getName).error(s"$tName=> $jobRequest timeout after $timeout")
          result = JobResult(JobStatus.TimeOut, ProcessResult(JobStatus.Failed, ex))
          receiveActor ! ProxyControlMsg(result)
          Thread.sleep(100)
      }
      receiveActor ! ProxyControlMsg(PoisonPill)
      receiveActor ! PoisonPill
      result.asInstanceOf[JobResult]
    }
    Future(getResult)(getBlockDispatcher())
  }
}
