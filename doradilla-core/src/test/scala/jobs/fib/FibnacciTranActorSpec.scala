package jobs.fib

import akka.testkit.TestProbe
import doracore.ActorTestClass
import doracore.core.msg.Job._
import doracore.core.msg.TranslationMsg.{TranslatedTask, TranslationError}
import doracore.util.CNaming
import jobs.fib.FibnacciTranActor.FibRequest
import play.api.libs.json.Json
import vars.ConstVarTest


/**
  * For jobs.jobs.jobs.fib in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/30
  */
class FibnacciTranActorSpec extends  ActorTestClass  {
  implicit val FibRequestFormat = Json.format[FibRequest]
  "Fibnacci translation actor " must{
    val probe = TestProbe()
    val fibTranActor = system.actorOf(FibnacciTranActor.fibnacciTranActorProps, CNaming.timebasedName( "FibnacciTran"))
    "Receive a normal request "in{
      val requestItem = JobRequest(ConstVarTest.fibTask,probe.ref,probe.ref)
      fibTranActor.tell(requestItem,probe.ref)
      probe.expectMsgPF(){
        case workerInfo: WorkerInfo=> workerInfo.actorName should be (classOf[FibWorkActor].getName)
      }
      probe.expectMsgPF(){
        case init:TranslatedTask => println(init)
      }
    }
    "Receive a wrong operation should return translation error" in {
      val wrongRequestItem = JobRequest(JobMsg("NotUknommmm",""),probe.ref,probe.ref)
      fibTranActor.tell(wrongRequestItem, probe.ref)
      probe.expectMsgPF(){
        case error:TranslationError => println(error)
      }
    }
    "Receive a wrong request data should return treanslation error" in {
      val wrongRequestItem = JobRequest(JobMsg("fibreq","{\"b\":10}"),probe.ref,probe.ref)
      fibTranActor.tell(wrongRequestItem, probe.ref)
      probe.expectMsgPF(){
        case error:TranslationError => println(error)
      }
    }
  }
}
