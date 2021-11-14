"start and run command " in {
  val backendServer = BackendServer.startup(Some(1600))
  backendServer.registFSMActor()
  val msg = TestVars.processCallMsgTest
  val processJob = JobMsg("SimpleProcess", msg)
  val res = BackendServer.runProcessCommand(processJob).map {
    res =>
      println(res)
      assert(true)
  }
  Await.ready(res, ConstVars.timeout1S * 10)
}
