package vars

import doradilla.msg.TaskMsg.TaskMsg
import jobs.fib.FibnacciTranActor.FibRequest
import play.api.libs.json.Json

/**
  * For `var` in doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/3/30
  */
object ConstVar {
  val fibTask = TaskMsg("fibreq", Json.toJson(FibRequest(10)).toString)
}
