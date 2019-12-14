package doradilla.back

import akka.actor.ActorRef
import akka.util.Timeout
import doracore.api.JobApi
import doracore.core.msg.Job.{JobMsg, JobRequest, JobResult}
import doracore.tool.receive.ReceiveActor
import doracore.util.CNaming
import doracore.vars.ConstVars

import scala.concurrent.{ExecutionContext, Future}

/**
  * For doradilla.back in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/12/14
  */
trait NamedJobRunner {
  this: BackendServer.type =>



  private def getNamedJobApi(jobName:String):JobApi={
    namedJobApiMap.get(jobName) match {
      case Some(jobApi) => jobApi
      case _=> val jobApi: JobApi = new JobApi(Some( getActorSystem()))
        namedJobApiMap +=(jobName ->jobApi)
        jobApi
    }
  }

  def runNamedProcessCommand(processJob: JobMsg,
                             jobName:String,
                             timeout: Timeout = ConstVars.longTimeOut,
                             priority: Option[Int] = None)(implicit ex: ExecutionContext): Future[JobResult] = {
    val jobApi = getNamedJobApi(jobName)
    val receiveActor = jobApi.actorSystem.actorOf(ReceiveActor.receiveActorProps, CNaming.timebasedName("Receive"))
    val processJobRequest = JobRequest(processJob, receiveActor, jobApi.processTranActor, priority)
    getProcessCommandFutureResult(processJobRequest, jobApi.defaultDriver, receiveActor,timeout)
  }
}
