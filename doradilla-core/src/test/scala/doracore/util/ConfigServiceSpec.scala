package doracore.util

import com.typesafe.config.ConfigFactory
import doracore.ActorTestClass
import doradilla.conf.DoraConf

/**
  * For doradilla.util in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/9
  */
class ConfigServiceSpec extends ActorTestClass{
  "ConfigService" must{
    val config = ConfigFactory.load()
    "return None when no value set in path" in{
      val notExistValue = ConfigService.getStringOpt(config, "NoExisted")
      notExistValue shouldBe (None)
    }

    "return Some when value is set " in {
      val existValue =  ConfigService.getStringOpt(config, "akka.actor.default-blocking-io-dispatcher.executor")
      existValue shouldBe (Some("thread-pool-executor"))
    }

    "return None when no config in path" in{
      val notExistValue = ConfigService.getConfigOpt(config, "NoExisted")
      notExistValue shouldBe (None)
    }

    "return None when no int config in path" in {
      val noExistInt = ConfigService.getIntOpt(config, "NoExisted")
      noExistInt shouldBe(None)
    }

    "return Some Int when int config in path" in{
      val fsmNumber = ConfigService.getIntOpt(config, "dora.fsmNumber")
      fsmNumber shouldBe (Some(1))
    }
    "return None to  Int when int config in path is not Int" in{
      val conf2= DoraConf.config(1888,"backend",Some("doradilla.fsm.timeout=3s"))
      val fsmNumber = ConfigService.getIntOpt(conf2, "doradilla.fsm.timeout")
      fsmNumber shouldBe (None)
    }
  }

}
