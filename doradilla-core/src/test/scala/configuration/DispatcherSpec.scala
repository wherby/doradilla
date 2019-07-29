package configuration

import akka.actor.ActorSystem
import akka.testkit.TestProbe
import doracore.core.msg.Job.JobResult
import doracore.tool.job.process.ProcessTranActor.SimpleProcessInit
import doracore.tool.job.process.ProcessWorkerActor
import doracore.util.CNaming
import doracore.vars.ConstVars
import doradilla.conf.DoraConf
import org.scalatest.{FlatSpec, Matchers}
import vars.ConstVarTest

class DispatcherSpec extends FlatSpec with Matchers{
  "ues blockDispatcher name " should
    "use customized dispatcher " in {
    implicit val actorSystem = ActorSystem("DisaptcherTest",DoraConf.config(1500,"back",Some("blocking-io-dispatcher {\n  type = Dispatcher\n  executor = \"thread-pool-executor\"\n  thread-pool-executor {\n    fixed-pool-size = 32\n  }\n  throughput = 1\n}")));
    actorSystem.dispatchers.hasDispatcher(ConstVars.blockDispatcherName) should be (true)
    val probe = TestProbe()
    val processWorkerActor = actorSystem.actorOf(ProcessWorkerActor.processTranActorProps,CNaming.timebasedName( "ProcessWorkerActorSpecWorker1"))
    val simpleProcessInit = SimpleProcessInit(ConstVarTest.processCallMsgTest,probe.ref)
    processWorkerActor ! simpleProcessInit
    Thread.sleep(2000)
    probe.expectMsgPF(){
      case jobResult: JobResult => println(jobResult)
    }
  }
}
