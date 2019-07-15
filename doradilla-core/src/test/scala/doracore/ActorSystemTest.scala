package doracore
import akka.actor.ActorSystem

/**
  * For doradilla in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/24
  *
  * Create a "Singleton" io.github.wherby.doradilla.test system. while io.github.wherby.doradilla.test start in sequence, then the isolation of the resource will be OK.
  */
 object ActorSystemTest {
  var actorSystem:Option[ActorSystem] = None
  var count = 0
  def  getActorSystem()={
    count = count +1
    actorSystem match {
      case None => actorSystem = Some(ActorSystem("AkkaQuickstartSpec"));
        actorSystem.get
      case _=> actorSystem.get
    }
  }

  def shutdowmSystem()={
    count = count -1
    if(count == 0){
     //actorSystem.get.terminate()
    }
  }
}
