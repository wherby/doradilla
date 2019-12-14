package doradilla.back.batch

import akka.actor.ActorRef
import akka.util.Timeout
import doracore.core.msg.Job.{JobMeta, JobMsg}
import doracore.tool.receive.ReceiveActor.QueryResult
import doracore.util.CNaming
import doracore.vars.ConstVars
import doradilla.back.BackendServer
import doradilla.back.batch.BatchProcessActor.{BatchJobResult, BatchProcessJob}
import doradilla.conf.Const
import akka.pattern.ask
import scala.concurrent.{ExecutionContext, Future}

/**
  * For doradilla.back in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/12/14
  */
trait BatchProcessor {
  this: BackendServer.type =>
  def startProcessBatchCommand(batchRequests: Seq[JobMsg],
                               backendServerOpt: Option[BackendServer] = None,
                               priority: Option[Int] = None, jobMetaOpt: Option[JobMeta] = None)(implicit ex: ExecutionContext): Option[ActorRef] = {
    val backendServer = getBackendServerForCommand(backendServerOpt)
    for (driverService <- backendServer.getActorProxy(Const.driverServiceName);
         processTranService <- backendServer.getActorProxy(Const.procssTranServiceName))
      yield {
        val actorSystem = backendServer.actorSystemOpt.get
        val receiveActor = actorSystem.actorOf(BatchProcessActor.batchProcessActorProp(), CNaming.timebasedName("BatchProcessActor"))
        val batchProcessJobRequest = BatchProcessJob(batchRequests, driverService, processTranService, priority, jobMetaOpt)
        receiveActor ! batchProcessJobRequest
        receiveActor
      }
  }

  def queryBatchProcessResult(batchProcessActor: ActorRef, timeout: Timeout = ConstVars.timeout1S)(implicit ex: ExecutionContext): Future[BatchJobResult] = {
    (batchProcessActor ? QueryResult()) (timeout).map {
      resultOpt =>
        resultOpt.asInstanceOf[BatchJobResult]
    }
  }
}
