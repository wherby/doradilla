package configuration

import akka.actor.ActorSystem
import doracore.vars.ConstVars
import doradilla.conf.DoraConf
import org.scalatest.{FlatSpec, Matchers}

class DispatcherSpec extends FlatSpec with Matchers{
  "ues blockDispatcher name " should
    "use customized dispatcher " in {
     val actorSystem = Some(ActorSystem("DisaptcherTest",DoraConf.config(1500,"back",Some("blocking-io-dispatcher {\n  type = Dispatcher\n  executor = \"thread-pool-executor\"\n  thread-pool-executor {\n    fixed-pool-size = 32\n  }\n  throughput = 1\n}"))));
     actorSystem.get.dispatchers.hasDispatcher(ConstVars.blockDispatcherName) should be (true)
  }
}
