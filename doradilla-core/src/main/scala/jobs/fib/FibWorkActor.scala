package jobs.fib

import akka.actor.ActorRef
import doradilla.base.BaseActor
import doradilla.msg.TaskMsg.{TaskResult, TaskStatus}
import jobs.fib.FibnacciTranActor.{FibAdd, FibInit, FibRequest, FibResult}
import play.api.libs.json.Json

/**
  * For jobs.jobs.fib in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/30
  */
class FibWorkActor(config: String) extends BaseActor {
  val endWith: Int = Json.parse(config).asOpt[FibRequest] match {
    case Some(fibRequest) if fibRequest.a >= 1 => fibRequest.a
    case _ => 0
  }
  var replyTo: ActorRef = null

  def handle(fibAdd: FibAdd) = {
    fibAdd.a match {
      case a if a < endWith => self ! FibAdd(fibAdd.a + 1, fibAdd.fa + fibAdd.fam1, fibAdd.fa)
      case a if a == endWith => replyTo ! TaskResult(TaskStatus.Finished ,Json.toJson(FibResult(a, fibAdd.fa)).toString())
      case _ => replyTo ! TaskResult(TaskStatus.Finished ,Json.toJson(FibResult(0, 0)).toString())
    }

  }

  override def receive: Receive = {
    case fibAdd: FibAdd => handle(fibAdd)
    case fibInit: FibInit => replyTo = fibInit.replyTo
      self ! fibInit.fibadd
  }
}
