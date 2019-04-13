package doradilla.api

import doradilla.ActorTestClass

/**
  * For doradilla.api in Doradilla
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2019/4/13
  */
class DriverApiSpec extends ActorTestClass{
  "Driver Api" must{
    "Return driver actor " in{
      val system = new SystemApi()with DriverApi
      system.driver.toString() should include ("driver")
    }
  }
}
