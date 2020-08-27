package doracore

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import doracore.util.{Par1, Par2, ProcessService}
import doracore.util.ProcessService.ProcessCallMsg
import doradilla.back.BackendServer
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

/**
  * For doradilla in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/24
  *
  * Design for io.github.wherby.doradilla.test actor system
  */
class ActorTestClass (_system: ActorSystem = ActorSystemTest.getActorSystem()) extends  TestKit(_system) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  override protected def beforeAll(): Unit = {
    /*val system = BackendServer.createBackendServer(Some(1600))
    BackendServer.backendServerMap +=(1600->system)*/
  }
  override def afterAll: Unit = {
    Thread.sleep(2000)
    ActorSystemTest.shutdowmSystem()
  }
}
