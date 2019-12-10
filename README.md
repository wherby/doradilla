Doradilla
===========================

[![Build Status](https://travis-ci.org/wherby/doradilla.svg?branch=master)](https://travis-ci.org/wherby/doradilla)[![codecov.io](https://codecov.io/github/wherby/doradilla/coverage.svg?branch=master)](https://codecov.io/github/wherby/doradilla?branch=master)


Doradilla-core is a job manage system.
Doradilla is a distributed system which is based on Akka cluster.

### Doradilla-Core

Doradilla-core is a job manage system which will handle the job request in reactive way.

### Questions about Doradilla

#### What's the problem the library resolve?

The library provide a reactive way to handle resource consuming(CPU, Memory, DB connection) tasks.

For example, an OCR application which will trigger OCR tasks based on requests, for each OCR task there needs one CPU core occupied. If there is no implementation of job management, the CPUs will be easily taken by OCR jobs. The CPU competition will easily slow down the processing and block other function.

What's the traditional way to solve the issue is create a job queue, and use a worker to takes job from the queue.

Is there any universal way to resolve this type of question and makes the implementation easy to use? 


Yes, just use the Doradilla library.

#### How the Doradilla library works?

Simple version: 

The Doradilla library use a queue to keep job requests and FSMActor will pull job request to process.  

Is the same way as traditional way?

Yes, but not, because the user will only aware of DriverActor, not aware of the library implementation. The example shows user don't need to know the doradilla's implementation, only call the job api. The Doradilla library will handle the travail work.

#### Why there is need JobTranslator?

For general purpose, ever complex job could be translate to simple job and so on. When you handle the complex job, you could design your JobTranslator to handle that job.

#### What's if I aready have ActorSystem in my project?

Well, you can only use doradcore library instead of use doradilla.


#### Message flow
![msgflow](./docs/doradilla-core/pics/msgflow.jpg)

See detail: [doradilla-core](/docs/doradilla-core/doradilla-core.md)



### Doradilla cluster usage


Doradilla provides distributed running environment which is based on Akka cluster. With same configuration as Akka cluster, Doradilla-core will running on Akka cluster node.

![Doradilla-cluster](./docs/doradilla-core/pics/dora-cluster.png)


### How to use

### How to run a process job

1.Run process job and get synchronize result:

```Scala
  "Baeckend server " should "start and run command " in {
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
```

2.Run process job and query result:

``` Scala
  "Run process Command " should  "start the command and qurey result " in {
    val backendServer = BackendServer.startup(Some(1600))
    backendServer.registFSMActor()
    val msg = TestVars.processCallMsgTest
    val processJob = JobMsg("SimpleProcess", msg)
    val receiveActor = BackendServer.startProcessCommand(processJob).get
    BackendServer.queryProcessResult(receiveActor).map {
      resultOpt =>
        assert(resultOpt == None)
    }
    Thread.sleep(2000)
    val res = BackendServer.queryProcessResult(receiveActor).map {
      resultOpt =>
        assert(resultOpt != None)
    }
    Await.ready(res, ConstVars.timeout1S)
  }
```

### For use defined implementation of reflection. 

User should defined their implementation of reflection, more information see [ProcessService](./docs/doradilla-core/util/ProcessService.md)

### More link about this library

[FSMActor workflow design](https://wherby.github.io/fsmactor-design/)