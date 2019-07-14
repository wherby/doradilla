Doradilla
===========================

[![Build Status](https://travis-ci.org/wherby/doradilla.svg?branch=master)](https://travis-ci.org/wherby/doradilla)


Doradilla is a distributed job manage system.

### Doradilla-Core

Doradilla-core is a job manage system which will handle the job request in reactive way.


### Message flow
![msgflow](./docs/doradilla-core/pics/msgflow.jpg)

See detail: [doradilla-core](/docs/doradilla-core/doradilla-core.md)


### How to use

### How to run a process job

1.Run process job and get synchronize result:

```Scala
  "Baeckend server " should "start and run command " in {
    val backendServer = BackendServer.startup(Some(1600))
    backendServer.registFSMActor()
    val msg = TestVars.processCallMsgTest
    BackendServer.runProcessCommand(msg).map{
      res=> println(res)
        assert(true)
    }
  }
```

2.Run process job and query result:

``` Scala
  "Run process Command " should  "start the command and qurey result " in {
    val backendServer = BackendServer.startup(Some(1600))
    backendServer.registFSMActor()
    val msg = TestVars.processCallMsgTest
    val receiveActor = BackendServer.startProcessCommand(msg).get
    BackendServer.queryProcessResult(receiveActor).map{
        resultOpt =>
          assert(resultOpt == None)
    }
    Thread.sleep(2000)
    BackendServer.queryProcessResult(receiveActor).map{
      resultOpt =>
        assert(resultOpt != None )
    }
  }
```

