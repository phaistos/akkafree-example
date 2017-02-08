package org.example

import akka.event.LoggingAdapter
import cats.free.Free
import cats.free.Free.liftF
import cats.arrow.FunctionK
import monix.eval.Task

object NF {

  // algebra
  sealed trait NameFreeA[A]
  case object First extends NameFreeA[String]
  case object Second extends NameFreeA[String]

  // free type and helpers
  type NameFree[A] = Free[NameFreeA, A]
  def first = liftF[NameFreeA, String](First)
  def second = liftF[NameFreeA, String](Second)
  def full = for {
    a <- first
    b <- second
  } yield s"$a $b"

  // wrap (normally some effectful process) result in a task
  def taskCompiler = new FunctionK[NameFreeA, Task] {
    def apply[A](fa: NameFreeA[A]): Task[A] =
      fa match {
        case First => Task.now("Joe")
        case Second => Task.now("Camel")
      }
  }

  // augment any previous task by logging the result
  def logCompiler(la: LoggingAdapter) = new FunctionK[Task, Task] {
    def apply[A](fa: Task[A]): Task[A] = fa.map(v => {la.info(s"Result of $fa is $v."); v})
  }

}
