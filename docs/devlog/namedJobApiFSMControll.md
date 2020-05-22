# FSMActor number control for named JobApi (Since "1.8.0.0")

## Why FSMActor number control need for named JobApi?

For named JobApi there will has only one FSMActor created at initialization.

So the increase and decrease FSMActor number function is added.

```scala
  sealed trait FSMControl

  case class FSMIncrease(increase: Int) extends FSMControl

  case class FSMDecrease(decrease: Int) extends FSMControl
  
    ...
  def handleFSMControl(fsmControl: FSMControl) = {
    fsmControl match {
      case FSMIncrease(num) if (num > 0  && num < 1000)=> for (_ <- 1 to num) {
        log.info("Increase FSMActor.")
        createOneFSMActor()
      }
      case FSMDecrease(num) => fsmToBeDecrease = fsmToBeDecrease + num
    }
  }
```

