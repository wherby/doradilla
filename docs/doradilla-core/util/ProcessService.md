# ProcessService


### For use defined implementation of refection. 

For callProcess function in doracore.util.ProcessService, the origin implementation is as below. This implementaion 
which will break the rule [CWE-470](https://cwe.mitre.org/data/definitions/470.html)

```scala
      lazy val aOpt = classLoaderOpt match {
        case Some(classloader) if processCallMsg.clazzName.indexOf("Processor")>=0 =>
          Some(Class.forName(processCallMsg.clazzName,false,classloader))
        case _ if processCallMsg.clazzName.indexOf("Processor" )>=0 =>
          Some(Class.forName(processCallMsg.clazzName))
        case _=>None
      }
```

User need to define their workaround implementation of nameToClassOpt

```scala
  var nameToClassOpt:(String, Option[ClassLoader]) => Option[Class[_]] = notImplement
```

There is a sample implementation in test folder doracore.util.ProcessServiceSpec

```scala
object ProcessServiceSpec{
  def processServiceNameToClassOpt(className: String,classLoaderOpt: Option[ClassLoader]): Option[Class[_]] ={
    classLoaderOpt match {
      case Some(classloader) if className.indexOf("Processor")>=0 =>
        Some(Class.forName(className,false,classloader))
      case _ if className.indexOf("Processor" )>=0 =>
        Some(Class.forName(className))
      case _=>None
    }
  }
}
class ProcessServiceSpec extends FlatSpec with Matchers {


  val processCallMsg = ProcessCallMsg("doracore.util.TestProcessor","addPar",Array(Par1(2).asInstanceOf[AnyRef],Par2(4).asInstanceOf[AnyRef]))
  "Process Service" should "return value "in {
    ProcessService.nameToClassOpt = ProcessServiceSpec.processServiceNameToClassOpt
    val result = ProcessService.callProcess(processCallMsg)
    result shouldBe(Right(Par1(6)))
  }

```

user need to define their implementation of reflection.

For the example as above which will not fix the CWE-470 for more information see [how-to-fix-cwe-470-use-of-externally-controlled-input-to-select-classes-or-code](https://stackoverflow.com/questions/51086638/how-to-fix-cwe-470-use-of-externally-controlled-input-to-select-classes-or-code)

There is one example of how to fix CWE-470:

```scala
object ProcessServiceSpec{

  val processNameSet :Map[String,Class[_]] = Map(
    "doracore.util.TestProcessor"-> (new TestProcessor).getClass,
    "doracore.util.TestProcessor2"->(new TestProcessor2).getClass,
    "doracore.util.TestProcesso3"->(new TestProcesso3).getClass,
  )

  def safeProcessServiceNameToClassOpt(className: String,classLoaderOpt: Option[ClassLoader]): Option[Class[_]] ={
    processNameSet.get(className)
  }
}
class ProcessServiceSpec extends FlatSpec with Matchers {


  val processCallMsg = ProcessCallMsg("doracore.util.TestProcessor","addPar",Array(Par1(2).asInstanceOf[AnyRef],Par2(4).asInstanceOf[AnyRef]))
  "Process Service" should "return value "in {
    ProcessService.nameToClassOpt = ProcessServiceSpec.safeProcessServiceNameToClassOpt()
    val result = ProcessService.callProcess(processCallMsg)
    result shouldBe(Right(Par1(6)))
  }
 ...
```