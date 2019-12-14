## ProcessService run with instance (since 1.7.3.1)

### Feature:

ProcessService could run instance without reflection

[ProcessService](https://github.com/wherby/doradilla/blob/2a9c8450c046681eefac8846119abf9f8268f950/doradilla-core/src/main/scala/doracore/util/ProcessService.scala)

```scala
  def getProcessMethod(processCallMsg: ProcessCallMsg): ProcessCallMsg => Either[AnyRef, AnyRef] = {
    processCallMsg.instOpt match {
      case Some(instance) => callMethodForObject
      case _ => callProcess
    }
  }
```

[Commit](https://github.com/wherby/doradilla/commit/2a9c8450c046681eefac8846119abf9f8268f950)

