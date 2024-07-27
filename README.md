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

Yes, but not, because the user will not aware of the library implementation. The example shows user call the job api. The Doradilla library will handle the travail work.

#### Why there is need JobTranslator?

For general purpose, every complex job could be translate to simple job and so on. When you handle the complex job, you could design your JobTranslator to handle that job.
The source code contains some examples of how to create JobTranslator.

#### What's if I aready have ActorSystem in my project?

Well, you can only use doradcore library instead of use doradilla.


#### Message flow
![msgflow](https://wherby.github.io/doradilla/introduction/msgflow.jpg)

See detail: [doradilla-core](/docs/doradilla-core/doradilla-core.md)



### Doradilla cluster usage


Doradilla provides distributed running environment which is based on Akka cluster. With same configuration as Akka cluster, Doradilla-core will running on Akka cluster node.

![Doradilla-cluster](https://wherby.github.io/doradilla/introduction/cluster.png)


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

### More document about this library:

[Doradilla](https://wherby.github.io/doradilla/)




## License and Terms of Use

### License Agreement

Effective July 25, 2024, the library is licensed under the AGPL-v3. Non-commercial use requires adherence to the current license terms.
Commercial use necessitates explicit authorization from the author. All library implementations must comply with the latest license within three months of this date.

### Terms of Use for Commercial Exploitation of Library Content

#### Commercial Use and Copyright

Any commercial use of library content without explicit written consent from the author constitutes a breach of copyright.
Such unauthorized use obligates the user to contribute 1% of the commercial income derived from the use of the library to the author.


#### Grace Period for License Transition

Existing commercial users of previous library versions have a three-month grace period to transition.
After this period, users must either cease commercial use or comply with the latest license.



