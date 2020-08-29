package doracore.tool.query

import akka.testkit.TestProbe
import doracore.ActorTestClass
import doracore.core.driver.DriverActor
import doracore.tool.query.QueryActor.{GetRoot, QueryRoot, RootResult}
import doracore.util.CNaming

/**
  * For doradilla.tool.query in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/5
  */
class QueryActorSpec extends  ActorTestClass  {
  "QueryActor " must {
    val driverName =  CNaming.timebasedName(  "QueryActorSpecDriver")
    val driver = system.actorOf(DriverActor.driverActorProps(), driverName)
    val probe = TestProbe("QueryActorProbe")
    val queryName = CNaming.timebasedName( "QueryActorSpecQueryActor")
    val queryActor = system.actorOf(QueryActor.queryActorProps,queryName)
    Thread.sleep(400)
    "return queried root actor when GetRoot with None " in {
      queryActor ! QueryRoot(driver)
      Thread.sleep(100)
      queryActor.tell(GetRoot(None),probe.ref)
      probe.expectMsgPF(){
        case rootResult: RootResult => println(rootResult)
          rootResult.rootMap.get(driver.path.toString) shouldBe a [Some[_]]
      }
    }
    "return queried root actor when GetRoot with path " in {
      queryActor ! QueryRoot(driver)
      Thread.sleep(100)
      queryActor.tell(GetRoot(Some(driver.path.toString)),probe.ref)
      probe.expectMsgPF(){
        case rootResult: RootResult => println(rootResult)
          rootResult.rootMap.get(s"akka://${system.name}/user/$driverName") shouldBe a [Some[_]]
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
