# Overview

## Doradilla-core 

Doradilla-core is a job manage system which will handle the job request in reactive way.

## Dependency

@@dependency[sbt,Maven,Gradle] {
  group="io.github.wherby"
  artifact="doradilla-core_2.12"
  version="1.7.3.1"
}


## What's the problem the library resolve?

The library provide a reactive way to handle resource consuming(CPU, Memory, DB connection) tasks.

For example, an OCR application which will trigger OCR tasks based on requests, for each OCR task there needs one CPU core occupied. If there is no implementation of job management, the CPUs will be easily taken by OCR jobs. The CPU competition will easily slow down the processing and block other function.

What's the traditional way to solve the issue is create a job queue, and use a worker to takes job from the queue.

Is there any universal way to resolve this type of question and makes the implementation easy to use? 

Yes, just use the Doradilla library.



## How the Doradilla library works?

Simple version: 

The Doradilla library use a queue to keep job requests and FSMActor will pull job request to process.  

Is the same way as traditional way?

Yes, but not, because the user will not aware of the library implementation. The example shows user call the job api. The Doradilla library will handle the travail work.


## Quick start

To use doradilla library to handle request see:

run job sync
: @@snip [runjob.scala](/docs/src/main/scala/runjob.md)

run job async
: @@snip [asyncrun.scala](/docs/src/main/scala/asyncrun.md)


For more usage see : 
@github[BackendSpec.scala](/doradilla-core/src/test/scala/app/BackendSpec.scala)