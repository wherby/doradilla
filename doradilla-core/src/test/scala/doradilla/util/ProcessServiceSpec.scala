package doradilla.util

import org.scalatest.AsyncFlatSpec

/**
  * For doradilla.util in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/7
  */
class ProcessServiceSpec extends AsyncFlatSpec{
    "ProcessService" should  "Run a sleep 0.5 second could result succss" in {
      val cmd = List("ping 127.0.0.1 -n 1")
      val resultFuture = ProcessService.runProcess(cmd)
      resultFuture.map {
        result => println(result)
          assert(result.exitValue==0)
      }
    }

}
