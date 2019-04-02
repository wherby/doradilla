Driver
=========


## Message

### RequestMsg

RequestMsg is the initial workflow message. DriverActor get the message, will create a ProxyActor to "handle" the request. The ProxyActor will not process the RequestMsg, but put the RequestMsg to QueueActor to process the RequestMsg. The ProxyActor will collect the result of the RequestMsg.

### FetchJob

FetchJob is the message which trigger DriverActor to get queued RequestMsg 
from QueueActor. If there are jobs in QueueActor, then then the job will be fetched to DriverActor, then DriverActor will send the job to FSMActor to process.

### RequestList

RequestList is the jobs which get from QueueActor.


## Properties

### QueueActor

QueueActor could be set when initialize the DriveActor which means the DriverActor will use an existed QueueActor. When there is no QueueActor set, the DriverActor will create an anonmous QueueActor.


### FSMActor

FSMActor will created when DriverActor initilized.