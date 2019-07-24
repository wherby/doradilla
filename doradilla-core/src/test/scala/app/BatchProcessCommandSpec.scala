package app

import doradilla.back.BackendServer
import doradilla.conf.TestVars
import org.scalatest.{AsyncFlatSpec, Matchers}

class BatchProcessCommandSpec extends AsyncFlatSpec  with  Matchers{
  "Run batched process command " should  "start batch job and query the result " in {
    val backendServer = BackendServer.startup(Some(1600))
    backendServer.registFSMActor()
    val batchMsg = Seq(TestVars.processCallMsgTest,TestVars.processCallMsgTest)
    val batchActor = BackendServer.startProcessBatchCommand(batchMsg).get
    BackendServer.queryBatchProcessResult(batchActor).map{
      resultOpt=>
        assert(resultOpt.results(0).jobResultOpt == None)
    }
    Thread.sleep(2000)
    BackendServer.queryBatchProcessResult(batchActor).map{
      resultOpt=>
        println(resultOpt.results.toList)
        assert(resultOpt.results(0).jobResultOpt != None)
    }
  }
}
