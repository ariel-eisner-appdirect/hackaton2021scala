package com.appdirect.prode.service

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, Promise}

object ProdeService extends ProdeCalculations {
  private val random = scala.util.Random
  val forecasts: ListBuffer[Forecast] = ListBuffer()
  val games: ListBuffer[Game] = ListBuffer()

  //

  def createGame(game: Game): Future[Game] = create("Create Game", game, games)

  def getGames: Future[List[Game]] = get("Get Games", games)

  def createForecast(forecast: Forecast): Future[Forecast] = create("Create Forecast", forecast, forecasts)

  def getForecasts: Future[List[Forecast]] = get("Get Forecasts", forecasts)

  def getScores: Future[List[Score]] = {
    val promise: Promise[List[Score]] = Promise[List[Score]]()

    simulateDelay("Get Score List", () => promise.success(calculateWinners(games.toList, forecasts.toList)))

    promise.future
  }

  //
  private def create[T](message: String, instance: T, collection: ListBuffer[T]): Future[T] = {
    val promise: Promise[T] = Promise[T]()

    simulateDelay(message, () => {
      Option(collection.indexOf(instance))
        .filter(_ > -1)
        .foreach(collection.remove)

      collection += instance

      promise.success(instance)
    })

    promise.future
  }

  def get[T](message: String, collection: ListBuffer[T]): Future[List[T]] = {
    val promise: Promise[List[T]] = Promise[List[T]]()

    simulateDelay(message, () => promise.success(collection.toList))

    promise.future
  }

  private def simulateDelay(name:String, execute: () => Unit): Unit = {
    val seconds = random.nextInt(1)
    println(s"Waiting $seconds to execute $name")
    Thread.sleep(seconds * 1000L)
    execute()
    println(s"$name was executed")
  }
}
