package doracore.util

import com.typesafe.config.ConfigFactory
import doracore.ActorTestClass

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
  }

}
