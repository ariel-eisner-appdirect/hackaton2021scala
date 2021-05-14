package com.appdirect.prode.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.stream.scaladsl.{Flow, Sink, Source}

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.concurrent.{Await, ExecutionContextExecutor, Future}

class ProdeServer (val port: Int, implicit val system: ActorSystem) {
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  private val serverSource: Source[Http.IncomingConnection, Future[Http.ServerBinding]] = Http().newServerAt("localhost", port).connectionSource()
  private var bindingFuture: Future[Http.ServerBinding] = _;

  def start(): Unit = {
    bindingFuture = serverSource.to(Sink.foreach { connection =>
        println("Accepted new connection from " + connection.remoteAddress)

        connection handleWith { Flow[HttpRequest].map(request => Await.ready(Routes.getRoutes(request), Duration.Inf).value.get.get) }
    }).run()

    bindingFuture.onComplete(log(s"Server running on localhost:$port", system.log.info))
  }

  def stop(): Unit = bindingFuture
    .map(binding => {
      log(s"Stopping on localhost:$port", system.log.info)()
      binding
        .terminate(FiniteDuration(5, TimeUnit.SECONDS))
        .onComplete(log(s"Server stopped on localhost:$port", system.log.info))
    })

  private def log(message: String, logger: String => Unit): Any => Unit = _ => logger(message)
}
