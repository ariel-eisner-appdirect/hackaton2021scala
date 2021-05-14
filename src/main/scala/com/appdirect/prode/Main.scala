package com.appdirect.prode

import akka.actor.ActorSystem
import com.appdirect.prode.server.ProdeServer

import scala.io.StdIn

object Main extends App {
  private val system: ActorSystem = ActorSystem()
  private val server = new ProdeServer(8080, system)

  server.start()

  StdIn.readLine()

  server.stop()
  system.terminate()

}
