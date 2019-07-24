package doradilla.back

import java.util.concurrent.Executors

import akka.actor.{ActorRef, PoisonPill, Props}
import doracore.base.BaseActor
import doracore.core.msg.Job.{JobMsg, JobRequest, JobResult}
import doracore.tool.receive.ReceiveActor
import doracore.tool.receive.ReceiveActor.{ ProxyControlMsg, QueryResult}
import doracore.util.CNaming
import doracore.util.ProcessService.ProcessCallMsg
import doradilla.back.BatchProcessActor.{BatchJobResult, BatchProcessJob, JobInfo}
import akka.pattern.ask
import akka.util.Timeout
import doracore.vars.ConstVars

import scala.concurrent.{Await, ExecutionContext, Future}

class BatchProcessActor extends BaseActor {
  implicit val ec = context.system.dispatchers.hasDispatcher(ConstVars.blockDispatcherName) match {
    case true => context.system.dispatchers.lookup(ConstVars.blockDispatcherName)
    case _ => ExecutionContext.fromExecutor(Executors.newFixedThreadPool(10))
  }
  var jobRecorder: Map[ActorRef, JobInfo] = Map()


  def handleBacthJob(batchProcessJob: BatchProcessJob) = {
    batchProcessJob.jobs.map {
      processCallMsg =>
        val processJob = JobMsg("SimpleProcess", processCallMsg)
        val actorSystem = context.system
        val receiveActor = actorSystem.actorOf(ReceiveActor.receiveActorProps, CNaming.timebasedName("ReceiverForBatch"))
        val processJobRequest = JobRequest(processJob, receiveActor, batchProcessJob.processTranServiceActor, batchProcessJob.priorityOpt)
        batchProcessJob.driverServiceActor.tell(processJobRequest, receiveActor)
        jobRecorder = jobRecorder.updated(receiveActor,JobInfo(processCallMsg, None))
    }
  }

  def handleQueryBatchResult(timeout: Timeout = ConstVars.timeout1S)(implicit ex: ExecutionContext) = {
    val result = Future.sequence(jobRecorder.keys.map {
      actorRef =>
        val jobInfo = jobRecorder(actorRef)
        jobInfo.jobResultOpt match {
          case None => (actorRef ? QueryResult()) (timeout).map {
            resultOpt =>
              resultOpt.asInstanceOf[Option[JobResult]] match {
                case Some(result) =>
                  jobRecorder = jobRecorder.updated(actorRef,JobInfo(jobInfo.processCallMsg, Some(result)))
                  actorRef ! ProxyControlMsg(PoisonPill)
                  actorRef ! PoisonPill
                  Some(result)
                case _ => None
              }
          }
          case _ => Future(None)
        }
    })
    Await.result(result, ConstVars.timeout1S)
    sender() ! BatchJobResult(jobRecorder.values.toSeq)
  }

  override def receive: Receive = {
    case batchProcessJob: BatchProcessJob => handleBacthJob(batchProcessJob)
    case queryResult: QueryResult => handleQueryBatchResult()
  }
}

object BatchProcessActor {

  case class BatchProcessJob(jobs: Seq[ProcessCallMsg], driverServiceActor: ActorRef, processTranServiceActor: ActorRef, priorityOpt: Option[Int] = None)

  case class JobInfo(processCallMsg: ProcessCallMsg, jobResultOpt: Option[JobResult])

  case class BatchJobResult(results: Seq[JobInfo])

  def batchProcessActorProp(): Props = {
    Props(new BatchProcessActor())
  }
}
