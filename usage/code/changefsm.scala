def changeFSMForNamedJob(jobName: String, num:Int)={
  val jobApi = getNamedJobApi(jobName)
  if(num >0){
    jobApi.defaultDriver ! FSMIncrease(num)
  }else{
    jobApi.defaultDriver ! FSMDecrease(Math.abs(num))
  }
}