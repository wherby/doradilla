package doracore
import akka.actor.ActorSystem
import doradilla.back.BackendServer
import doradilla.conf.DoraConf

/**
  * For doradilla in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/24
  *
  * Create a "Singleton" io.github.wherby.doradilla.test system. while io.github.wherby.doradilla.test start in sequence, then the isolation of the resource will be OK.
  */
 object ActorSystemTest {
  var actorSystemOpt:Option[ActorSystem] = None
  var count = 0
  def  getActorSystem()={
    count = count +1
    actorSystemOpt match {
      case None =>
        val system = BackendServer.createBackendServer(Some(1600))
        BackendServer.backendServerMap +=(1600->system)
        system.registFSMActor()
        actorSystemOpt =system.actorSystemOpt
        actorSystemOpt.get
      case _=> actorSystemOpt.get
    }
  }

  def shutdowmSystem()={
    count = count -1
    if(count == 0){
     //actorSystem.get.terminate()
    }
  }
}
