package jobs.fib

import `var`.ConstVar
import akka.actor.Props
import akka.testkit.TestProbe
import doradilla.ActorTestClass
import doradilla.msg.TaskMsg._
import jobs.fib.FibnacciTranActor.{FibInit, FibRequest}
import play.api.libs.json.Json


/**
  * For jobs.fib in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/30
  */
class FibnacciTranActorSpec extends  ActorTestClass  {
  implicit val FibRequestFormat = Json.format[FibRequest]
  "Fibnacci translation actor " must{
    val proxy = TestProbe()
    val fibTranActor = system.actorOf(Props(new FibnacciTranActor), "FibnacciTran")
    "Receive a normal request "in{
      val requestItem = RequestMsg(ConstVar.fibTask,proxy.ref,proxy.ref)
      fibTranActor.tell(requestItem,proxy.ref)
      proxy.expectMsgPF(){
        case workerInfo: WorkerInfo=> workerInfo.actorName should be (classOf[FibWorkActor].getName)
      }
      proxy.expectMsgPF(){
        case init:TranslatedTask => println(init)
      }
    }
    "Receive a wrong operation should return translation error" in {
      val wrongRequestItem = RequestMsg(TaskMsg("NotUknommmm",""),proxy.ref,proxy.ref)
      fibTranActor.tell(wrongRequestItem, proxy.ref)
      proxy.expectMsgPF(){
        case error:TranslationError => println(error)
      }
    }
    "Receive a wrong request data should return treanslation error" in {
      val wrongRequestItem = RequestMsg(TaskMsg("fibreq","{\"b\":10}"),proxy.ref,proxy.ref)
      fibTranActor.tell(wrongRequestItem, proxy.ref)
      proxy.expectMsgPF(){
        case error:TranslationError => println(error)
      }
    }
  }
}
