package doracore.api

import doracore.ActorTestClass
import doracore.core.msg.Job
import doracore.core.msg.Job.{JobResult, JobStatus}
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
      val jobApi = new JobApi(Some(system))
      val jobList = Seq(ConstVarTest.command)
      val resultFuture =   Future.sequence(jobList.map{
        job => jobApi.processCommand(job)
      })
     resultFuture.map{
        result => println(result)
          result shouldBe a [JobResult]
      }
      val jobSeq =Await.result(resultFuture, ConstVarTest.timeout10S)
      jobSeq(0) shouldBe a [JobResult]
      jobSeq(0).taskStatus should (be (JobStatus.Finished) or be (JobStatus.Failed))
    }

    "return result for call processTran api" in{

      val jobApi = new JobApi(Some(system))
      val jobList = Seq(ConstVarTest.processCallMsgTest,ConstVarTest.processCallMsgTest)
      val resultFuture = Future.sequence(jobList.map{
        job => jobApi.runProcessCommand(job)
      })
      resultFuture.map{
        result => println(result)
      }
      val jobSeq =Await.result(resultFuture, ConstVarTest.timeout10S)
      jobSeq(0) shouldBe a [JobResult]
      jobSeq(0).taskStatus should (be (JobStatus.Finished) or be (JobStatus.Failed))
    }
  }
}
