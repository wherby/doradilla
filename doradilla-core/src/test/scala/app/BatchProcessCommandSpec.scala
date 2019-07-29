package app

import doracore.ActorTestClass
import doracore.core.msg.Job.JobMeta
import doracore.vars.ConstVars
import doradilla.back.BackendServer
import doradilla.conf.{DoraConf, TestVars}
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

class BatchProcessCommandSpec extends ActorTestClass  {
  "Run batched process command " must {
    "start batch job and query the result " in {
      val backendServer = BackendServer.startup(Some(1600))
      backendServer.registFSMActor()
      val batchMsg = Seq(TestVars.processCallMsgTest, TestVars.processCallMsgTest)
      val batchActor = BackendServer.startProcessBatchCommand(batchMsg,jobMetaOpt = Some(JobMeta("aaaa"))).get
      BackendServer.startProcessBatchCommand(batchMsg).get
      BackendServer.queryBatchProcessResult(batchActor).map {
        resultOpt =>
          assert(resultOpt.results(0).jobResultOpt == None)
      }
      Thread.sleep(4000)
      val res = BackendServer.queryBatchProcessResult(batchActor).map {
        resultOpt =>
          println(resultOpt.results.toList)
          assert(resultOpt.results(0).jobResultOpt != None)
      }
      BackendServer.queryBatchProcessResult(batchActor)
      Await.ready(res, ConstVars.timeout1S * 10)
    }

    "start batch job and query the result use user specified dispatcher  " in {
      val newConf= DoraConf.config(1400,"back",Some("blocking-io-dispatcher {\n  type = Dispatcher\n  executor = \"thread-pool-executor\"\n  thread-pool-executor {\n    fixed-pool-size = 32\n  }\n  throughput = 1\n}"))
      val backendServer = BackendServer.startup(Some(1400),Some(newConf))
      val systemopt= backendServer.actorSystemOpt
      val ress =systemopt.get.dispatchers.hasDispatcher(ConstVars.blockDispatcherName)
       assert(ress == true)
      backendServer.registFSMActor()
      val batchMsg = Seq(TestVars.processCallMsgTest, TestVars.processCallMsgTest)
      val batchActor = BackendServer.startProcessBatchCommand(batchMsg,jobMetaOpt = Some(JobMeta("aaaa"))).get
      BackendServer.startProcessBatchCommand(batchMsg).get
      BackendServer.queryBatchProcessResult(batchActor).map {
        resultOpt =>
          assert(resultOpt.results(0).jobResultOpt == None)
      }
      Thread.sleep(4000)
      val res = BackendServer.queryBatchProcessResult(batchActor).map {
        resultOpt =>
          println(resultOpt.results.toList)
          assert(resultOpt.results(0).jobResultOpt != None)
      }
      BackendServer.queryBatchProcessResult(batchActor)
      Await.ready(res, ConstVars.timeout1S * 10)
    }
  }
}