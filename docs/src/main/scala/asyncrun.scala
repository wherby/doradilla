"start the command and qurey result " in {
  ProcessService.nameToClassOpt = ProcessServiceSpec.safeProcessServiceNameToClassOpt
  val backendServer = BackendServer.startup(Some(1600))
  backendServer.registFSMActor()
  val msg = TestVars.processCallMsgTest
  val processJob = JobMsg("SimpleProcess", msg)
  val receiveActor = BackendServer.startProcessCommand(processJob).get
  val res= BackendServer.queryProcessResult(receiveActor).map {
    result =>
      (result.result.asInstanceOf[ProcessResult]).jobStatus shouldBe (JobStatus.Finished)
      println(result)
  }

  Await.ready(res, ConstVars.timeout1S*4)
}