import scala.collection.mutable

(0 to 3).map(println)

implicit val ord: Ordering[(Any,Int)] = Ordering.by(_._2)

 val queue = mutable.PriorityQueue[(Any,Int)]()

queue.enqueue(("aa",3))
queue.enqueue(("bb",2))
queue.enqueue(("cc",1))
queue.enqueue(("dd",3))
println(queue)
println(queue.take(30))
println(queue)
println(queue.drop(30))
println(queue)
println(queue.dequeue())
println(queue.dequeue())
println(queue.dequeue())
println(queue.dequeue())
//println(queue.dequeue())
println(queue)


