package doradilla.util

import org.scalatest.AsyncFlatSpec
import vars.ConstVarTest

/**
  * For doradilla.util in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/7
  */
class ProcessServiceSpec extends AsyncFlatSpec{
    "ProcessService" should  "Run a sleep 0.5 second could result succss" in {

      val resultFuture = ProcessService.runProcess(ConstVarTest.cmdWin, ConstVarTest.cmdLinux)
      resultFuture.map {
        result => println(result)
          assert(result.exitValue==0)
      }
    }

}
