# JobApi

## What's JobApi

JobApi is an interface and mixin of implementation of key functions in doradilla.

Let's see what's JobApi:

@@ snip [JobApi implementation](/doradilla-core/src/main/scala/doracore/api/JobApi.scala)

The Job Api mixin SystemApi, DriverApi and TranslationApi(CommmandTranApi and ProcessTranApi).

When create a JobApi:

1. If no AkkaSystem is passed in, there will create a AkkaSystem; if an AkkaSystem is passed in, the 
AkkaSystem will be used. [SystemApi]

2. A DriveActor will be created in the AkkaSystem. And a QueueActor will created by the DriverActor. 
A FSMActor will be created and linked to the QueueActor [DriverApi]

3. Some default TranslationActor(CommandTranActor, ProcessTranActor) will be created.[CommandTranApi, ProcessTranApi]

![Job Api](pic/jobapi.png)

Code implementation: 

SystemApi
: @@snip [SystemAPi](/doradilla-core/src/main/scala/doracore/api/SystemApi.scala)

DriverApi
: @@snip [DriverApi](/doradilla-core/src/main/scala/doracore/api/DriverApi.scala)

CommandTranApi
: @@snip [CommandTranApi](/doradilla-core/src/main/scala/doracore/api/CommandTranApi.scala)

ProcessTranApi
: @@snip [CommandTranApi](/doradilla-core/src/main/scala/doracore/api/ProcessTranApi.scala)



