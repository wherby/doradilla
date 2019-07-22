Dispatcher
========


# Dispatchers

In doradilla, there are two types dispatchers to be used:

## 1. Akka's default dispatcher

Akka's default dispatcher is used for none-blocking io dispatcher. You could customise the dispatcher according to [Akka Document](https://doc.akka.io/docs/akka/current/dispatchers.html).


## 2. Blocking-io dispatcher

Blocking IO thread (the worker's thread) use dispatcher as below:

``` Scala 
class WorkerActor extends BaseActor {
  implicit val ec = context.system.dispatchers.hasDispatcher(ConstVars.blockDispatcherName) match {
    case true => context.system.dispatchers.lookup(ConstVars.blockDispatcherName)
    case _=> ExecutionContext.fromExecutor(Executors.newFixedThreadPool(10))
  }
  ....
```
if user doesn't define blockDispatcherName in configuration file, newFixedThreadPool will be used as blocking io dispatcher.  For more reference see [Akka Document](https://doc.akka.io/docs/akka/current/dispatchers.html).