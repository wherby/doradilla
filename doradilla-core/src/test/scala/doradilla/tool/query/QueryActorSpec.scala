package doradilla.tool.query

import akka.actor.Props
import akka.testkit.TestProbe
import doradilla.ActorTestClass
import doradilla.core.driver.DriverActor
import doradilla.core.queue.QueueActor
import doradilla.tool.query.QueryActor.{GetRoot, QueryRoot, RootResult}

/**
  * For doradilla.tool.query in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/5
  */
class QueryActorSpec extends  ActorTestClass  {
  "QueryActor " must {
    val driver = system.actorOf(DriverActor.driverActorProps(), "QueryActorSpecDriver")
    val probe = TestProbe("QueryActorProbe")
    val queryActor = system.actorOf(QueryActor.queryActorProps,"QueryActorSpecQueryActor")
    Thread.sleep(400)
    "return queried root actor when GetRoot with None " in {
      queryActor ! QueryRoot(driver)
      Thread.sleep(100)
      queryActor.tell(GetRoot(None),probe.ref)
      probe.expectMsgPF(){
        case rootResult: RootResult => println(rootResult)
          rootResult.rootMap.get("akka://AkkaQuickstartSpec/user/QueryActorSpecDriver") shouldBe a [Some[_]]
      }
    }
    "return queried root actor when GetRoot with path " in {
      queryActor ! QueryRoot(driver)
      Thread.sleep(100)
      queryActor.tell(GetRoot(Some("akka://AkkaQuickstartSpec/user/QueryActorSpecDriver")),probe.ref)
      probe.expectMsgPF(){
        case rootResult: RootResult => println(rootResult)
          rootResult.rootMap.get("akka://AkkaQuickstartSpec/user/QueryActorSpecDriver") shouldBe a [Some[_]]
      }
    }

    "return queried root actor when GetRoot with not existed path " in {
      queryActor ! QueryRoot(driver)
      Thread.sleep(100)
      queryActor.tell(GetRoot(Some("akka://AkkaQuickstartSpec/user/NotExist")),probe.ref)
      probe.expectMsgPF(){
        case rootResult: RootResult => println(rootResult)
          rootResult.rootMap.get("akka://AkkaQuickstartSpec/user/QueryActorSpecDriver") shouldBe(None)
          rootResult.rootMap.keys.toSeq.length should be (0)
      }
    }
  }
}
