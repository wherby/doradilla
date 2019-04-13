package doradilla.api

import doradilla.ActorTestClass
import vars.ConstVarTest

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}

/**
  * For doradilla.api in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/13
  */
class JobApiSpec extends ActorTestClass{
  "JobApi " must{
    "return result " in{
      val jobApi = new JobApi()
      val jobList = Seq(ConstVarTest.command,ConstVarTest.command,ConstVarTest.command)
      val resultFuture =   Future.sequence(jobList.map{
        job => jobApi.processCommand(job)
      })
     resultFuture.map{
        result => println(result)
      }
      Await.result(resultFuture, ConstVarTest.timeout10S)
    }
  }
}
