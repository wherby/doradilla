package doradilla

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

/**
  * For doradilla in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/24
  *
  * Design for io.github.wherby.doradilla.test actor system
  */
class ActorTestClass (_system: ActorSystem = ActorSystemTest.getActorSystem()) extends  TestKit(_system) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll: Unit = {
    ActorSystemTest.shutdowmSystem()
  }
}
