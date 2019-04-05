package fib

import akka.actor.Props
import akka.testkit.TestProbe
import doradilla.ActorTestClass
import doradilla.msg.Job._
import jobs.fib.{FibWorkActor, FibnacciTranActor}
import jobs.fib.FibnacciTranActor.FibRequest
import play.api.libs.json.Json
import vars.ConstVar


/**
  * For jobs.jobs.fib in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/30
  */
class FibnacciTranActorSpec extends  ActorTestClass  {
  implicit val FibRequestFormat = Json.format[FibRequest]
  "Fibnacci translation actor " must{
    val proxy = TestProbe()
    val fibTranActor = system.actorOf(Props(new FibnacciTranActor), "FibnacciTran")
    "Receive a normal request "in{
      val requestItem = JobRequest(ConstVar.fibTask,proxy.ref,proxy.ref)
      fibTranActor.tell(requestItem,proxy.ref)
      proxy.expectMsgPF(){
        case workerInfo: WorkerInfo=> workerInfo.actorName should be (classOf[FibWorkActor].getName)
      }
      proxy.expectMsgPF(){
        case init:TranslatedTask => println(init)
      }
    }
    "Receive a wrong operation should return translation error" in {
      val wrongRequestItem = JobRequest(JobMsg("NotUknommmm",""),proxy.ref,proxy.ref)
      fibTranActor.tell(wrongRequestItem, proxy.ref)
      proxy.expectMsgPF(){
        case error:TranslationError => println(error)
      }
    }
    "Receive a wrong request data should return treanslation error" in {
      val wrongRequestItem = JobRequest(JobMsg("fibreq","{\"b\":10}"),proxy.ref,proxy.ref)
      fibTranActor.tell(wrongRequestItem, proxy.ref)
      proxy.expectMsgPF(){
        case error:TranslationError => println(error)
      }
    }
  }
}
