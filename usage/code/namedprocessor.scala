def runProcess(paras : List[AnyRef], clazzName:String, methodName:String, prioritySet: Option[Int] = None) ={
    ....
    val namedSevice = Seq("com.pwc.ds.cidr.project.creditreview.processors.OcrPlusProcessor")
    if(namedSevice.contains(clazzName)){
      BackendServer.runNamedProcessCommand(jobMsg,OCRJOBStr, priority = prioritySet,timeout = setTimeOut).map{
        ....
    }else{
      BackendServer.runProcessCommand(jobMsg,priority = prioritySet,timeout = setTimeOut).map{
        ...
    }
  }