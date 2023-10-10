package doradilla.back

import akka.util.Timeout
import doracore.api.JobApi
import doracore.core.driver.DriverActor.{FSMDecrease, FSMIncrease}
import doracore.core.msg.Job.{JobMeta, JobMsg, JobRequest, JobResult}
import doracore.tool.receive.ReceiveActor
import doracore.util.{AppDebugger, CNaming}
import doracore.vars.ConstVars

import javax.print.attribute.standard.JobName
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
      case _=>
        AppDebugger.log(s"Start new JobApi for $jobName",Some("getNamedJobApi"))
        val jobApi: JobApi = new JobApi(Some( getActorSystem()))
        namedJobApiMap +=(jobName ->jobApi)
        jobApi
    }
  }

  def runNamedProcessCommand(processJob: JobMsg,
                             jobName:String,
                             timeout: Timeout = ConstVars.longTimeOut,
                             priority: Option[Int] = None,
                             metaOpt:Option[JobMeta] =None)(implicit ex: ExecutionContext): Future[JobResult] = {
    val jobApi = getNamedJobApi(jobName)
    val receiveActor = jobApi.actorSystem.actorOf(ReceiveActor.receiveActorProps, CNaming.timebasedName("Receive"))
    val processJobRequest = JobRequest(processJob, receiveActor, jobApi.processTranActor, priority,metaOpt)
    getProcessCommandFutureResult(processJobRequest, jobApi.defaultDriver, receiveActor,timeout)
  }

  def changeFSMForNamedJob(jobName: String, num:Int)={
    val jobApi = getNamedJobApi(jobName)
    if(num >0){
      jobApi.defaultDriver ! FSMIncrease(num)
    }else{
      jobApi.defaultDriver ! FSMDecrease(Math.abs(num))
    }
  }
}
