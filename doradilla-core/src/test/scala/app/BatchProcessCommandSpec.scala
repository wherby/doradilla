package app

import akka.actor.ActorSystem
import doracore.ActorTestClass
import doracore.core.msg.Job.{JobMeta, JobMsg}
import doracore.vars.ConstVars
import doradilla.back.BackendServer
import doradilla.conf.{DoraConf, TestVars}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

class BatchProcessCommandSpec extends ActorTestClass  {
  "Run batched process command " must {
    val batchMsg = Seq(JobMsg("SimpleProcess",TestVars.processCallMsgTest), JobMsg("SimpleProcess",TestVars.processCallMsgTest))
    "start batch job and query the result " in {
      val backendServer = BackendServer.startup(Some(1600))
      backendServer.registFSMActor()

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
