package org.example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.io.StdIn
import scala.util.{Success, Failure}
import monix.cats._

object Akkafree {
  def main(args: Array[String]) {

    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()


    import NF._
    import monix.execution.Scheduler.Implicits.global

    // uncomment the rest of this line to add logging via composition
    val compiler = taskCompiler // andThen logCompiler(system.log)

    val route =
      get {
        path("first") {
          val task = first.foldMap(compiler)
          onComplete(task.runAsync) {
            case Success(s) => complete(s)
            case Failure(ex) => failWith(ex)
          }
        } ~
        path("second") {
          val task = second.foldMap(compiler)
          onComplete(task.runAsync) {
            case Success(s) => complete(s)
            case Failure(ex) => failWith(ex)
          }
        } ~
        path("full") {
          val task = full.foldMap(compiler)
          onComplete(task.runAsync) {
            case Success(s) => complete(s)
            case Failure(ex) => failWith(ex)
          }
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8090)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

}