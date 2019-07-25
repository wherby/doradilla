package app

import doracore.ActorTestClass
import doracore.vars.ConstVars
import doradilla.back.BackendServer
import doradilla.conf.TestVars
import org.scalatest.Matchers

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random

class BatchProcessCommandSpec extends ActorTestClass with Matchers {
  "Run batched process command " should {
    "start batch job and query the result " in {
      val backendServer = BackendServer.startup(Some(1600 + Random.nextInt(1000)))
      backendServer.registFSMActor()
      val batchMsg = Seq(TestVars.processCallMsgTest, TestVars.processCallMsgTest)
      val batchActor = BackendServer.startProcessBatchCommand(batchMsg).get
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
      Await.ready(res, ConstVars.timeout1S * 10)
    }
  }
}
