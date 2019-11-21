package doracore.api

import akka.actor.ActorSystem

trait ActorSystemApi {
  def getActorSystem():ActorSystem
}
