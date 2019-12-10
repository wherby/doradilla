Array(1)==Array(1)
Array(1).sameElements(Array(1))

def asOption[A](a:A) = Some(a)
def asOption2[A](a:A):Option[A] = Some(a)

def foo1()= if(false) throw  new Exception else 2
def foo2() = {
  val a = throw new Exception
  if(false) a else 2
}

/*
def parseInt(str:String):Int = str.toInt
parseInt("NoInt")
*/


/*class Foo(i:Int)

class Bar(i:Int, s:String) extends Foo(i)

new Bar(1,"foo") == new Bar(1,"foo")*/

/*case class Foo(i:Int)

class Bar(i:Int, s:String) extends Foo(i)

new Bar(1,"foo") == new Bar(1,"foo")*/

/*sealed trait Status extends Product with
 Serializable
object Status{
  case object Ok extends Status
  case object Nok extends Status
}

List(Status.Ok,Status.Nok)*/


/*sealed trait Status
object Status{
  case object Ok extends Status
  case object Nok extends Status
}

List(Status.Ok,Status.Nok)*/

/*object Status extends Enumeration{
  val Ok,Nok =Value
}

def foo(w :Status.Value):Unit = w match {
  case Status.Ok =>print("OK")
}

foo(Status.Nok)*/

/*
sealed trait Status
object Status{
  case object Ok extends Status
  case object Nok extends Status
}


def foo(w :Status):Unit = w match {
  case Status.Ok =>print("OK")
}*/

/*
sealed trait Foo

class Bar extends  Foo

class FooBar extends  Bar*/
/*

sealed trait Foo

final class Bar extends  Foo

class FooBar extends  Bar*/
