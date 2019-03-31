package scenario

import akka.actor.Props
import akka.testkit.TestProbe
import doradilla.ActorTestClass
import doradilla.driver.DriverActor
import doradilla.msg.TaskMsg.RequestMsg
import jobs.fib.FibnacciTranActor
import vars.ConstVar

/**
  * For scenario in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/31
  */
class PostJobToDriverSpec extends  ActorTestClass  {
  "Post FibJob to Driver will start Fib task" must{
    val fibTran = system.actorOf(Props(new FibnacciTranActor))
    val driver = system.actorOf(Props(new DriverActor()))
    val probe = TestProbe()

    "driver actor will handle RequestMsg " in{
       for(i <- 0 to 20 ){
         val request = RequestMsg(ConstVar.fibTaskN(i),probe.ref,fibTran)
         driver ! request
       }
      for(i <- 0 to 20 ){
      probe.expectMsgPF(){
        case msg=> println(msg)
      }
      }

      val msgs = receiveN(21)
      msgs.map(println)
    }
  }
}
