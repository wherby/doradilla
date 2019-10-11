package doracore.tool.job.worker

import akka.actor.{ActorRef, Cancellable}
import doracore.base.BaseActor
import doracore.core.msg.WorkerMsg.TickMsg
import doracore.vars.ConstVars
import scala.concurrent.{Future}
import scala.util.{Try}

/**
  * For doradilla.tool.job.worker in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/13
  */
class WorkerActor extends BaseActor with ExtractResultTrait with BlockIODispatcher {
  implicit val ec = GetBlockIODispatcher


  var replyToOpt: Option[ActorRef] = None
  var futureResultOpt: Option[Future[Any]] = None
  var cancelableSchedulerOpt: Option[Cancellable] = None
  val tickTime = ConstVars.tickTime


  def cancelScheduler(): Option[Boolean] = {
    cancelableSchedulerOpt.map({
      cancelableScheduler => cancelableScheduler.cancel()
    })
  }

  def doSuccess(executeResultEither: Try[Any]): Option[Unit] = {
    cancelScheduler()
    replyToOpt.map {
      replyTo =>
        val jobResult = extractJobResult(executeResultEither)
        replyTo ! jobResult
    }
  }


  def handleTickMsg(): Option[Any] = {
    futureResultOpt.map {
      futureResult =>
        futureResult.value.map {
          any => doSuccess(any)
        }
    }
  }

  override def receive: Receive = {
    case msg: TickMsg => handleTickMsg()
  }
}
