Doradilla 
============


## What's doradilla?


Doradilla is a job manage system which will manage a job in reactive way.

### Msg Flow
![msgflow](./pics/msgflow.jpg)

### Driver

Driver actor will receive JobRequest messge from user and create a JobProxyActor,
then send the JobProxyActot's reference to user


### ProxyActor

ProxyActor will forward the JobRequest to QueueActor, and monitor the JobStatus




