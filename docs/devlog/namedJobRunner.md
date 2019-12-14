## Named Job Runner (Since "1.7.3.0")

### Feature:

when process job with name parameter, the job with use named job api

If the named job api is not created, the api will create the named job api(driverActor, fsmActor etc.)

Jobs with same name will process in same named jobapi. 
More example see [example](https://github.com/wherby/doradilla/blob/c7033d614fd93211c0b33aad650492bf15e5b36d/doradilla-core/src/test/scala/app/NamedJobRunnerSpec.scala)






[commit](https://github.com/wherby/doradilla/commit/c7033d614fd93211c0b33aad650492bf15e5b36d)