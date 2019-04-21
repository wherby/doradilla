package doradilla.api

import akka.testkit.TestProbe
import doradilla.ActorTestClass
import doradilla.core.msg.Job.{JobRequest, JobResult}
import doradilla.core.msg.JobControlMsg.ResetDriver
import doradilla.core.queue.QueueActor
import vars.ConstVarTest

/**
  * For doradilla.api in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/13
  */
class DriverApiSpec extends ActorTestClass{
  "Driver Api" must{
    "Return driver actor " in{
      val system = new SystemApi()with DriverApi
      system.defaultDriver.toString() should include ("driver")
    }

    "Driver Api will use specified queue when queueActor is set" in {
      val proxy = TestProbe()
      val systemApi = new SystemApi(Some(system))with DriverApi with CommandTranApi
      val queueActor = systemApi.actorSystem.actorOf(QueueActor.queueActorProps,"DriverApiSpecQueue1")
      systemApi.queueActorSet = Some(queueActor)
      val commandRequest = JobRequest(ConstVarTest.commandJob,proxy.ref, systemApi.translatedActor)
      queueActor ! commandRequest
      systemApi.defaultDriver ! commandRequest
      proxy.expectMsgPF(){
        case msg => println(msg)
          msg
      }
      proxy.expectMsgPF(){
        case msg => println(msg)
          msg
      }
      proxy.expectMsgPF(){
        case msg => println(msg)
          msg
      }
      systemApi.defaultDriver ! ResetDriver()
      proxy.expectMsgPF(){
        case msg => println(msg)
          msg
      }
    }
  }
}
