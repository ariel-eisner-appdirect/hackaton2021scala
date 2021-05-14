package com.appdirect.prode.server

import akka.http.scaladsl.model.HttpMethods.{GET, POST}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpRequest, HttpResponse, Uri}
import com.appdirect.prode.service.{ForecastList, Game, GameList, ProdeService, ScoreList}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object Routes extends JsonSupport {

  private def games(request: HttpRequest): Future[HttpResponse] = request match {
    // Create Game
    case HttpRequest(POST, Uri.Path("/games"), _, _, _) =>
      ProdeService.createGame(toJson(request, gameJson))
        .map(game => HttpResponse(
            entity = HttpEntity(
              ContentTypes.`application/json`,
              gameJson.write(game).compactPrint
            )
          )
        )
    // List Games
    case HttpRequest(GET, Uri.Path("/games"), _, _, _) =>
      ProdeService.getGames
        .map(games => HttpResponse(
            entity = HttpEntity(
              ContentTypes.`application/json`,
              gameList.write(GameList(games)).compactPrint
            )
          )
        )

    case _: HttpRequest => null
  }

  private def forecasts(request: HttpRequest): Future[HttpResponse] = request match {
    // Create Forecast
    case HttpRequest(POST, Uri.Path("/forecasts"), _, _, _) =>
      ProdeService.createForecast(toJson(request, forecastJson))
        .map(game => HttpResponse(
            entity = HttpEntity(
              ContentTypes.`application/json`,
              forecastJson.write(game).compactPrint
            )
          )
        )

    // List Forecast
    case HttpRequest(GET, Uri.Path("/forecasts"), _, _, _) =>
      ProdeService.getForecasts
        .map(forecasts => HttpResponse(
            entity = HttpEntity(
              ContentTypes.`application/json`,
              forecastList.write(ForecastList(forecasts)).compactPrint
            )
          )
        )

    case _: HttpRequest => null
  }

  private def score(request: HttpRequest): Future[HttpResponse] = request match {
    // Calculate score
    case HttpRequest(GET, Uri.Path("/scores"), _, _, _) =>
      ProdeService.getScores
        .map(scores => HttpResponse(
            entity = HttpEntity(
              ContentTypes.`application/json`,
              scoreList.write(ScoreList(scores)).compactPrint
            )
          )
        )

    case _: HttpRequest => null
  }

  private def notFound(request: HttpRequest): Future[HttpResponse] = Future.successful(HttpResponse(
    status = 404,
    entity = HttpEntity(
      ContentTypes.`application/json`,
      "{\"message\": \"%s\"}".format("Not Found")
    )
  ))

  private def badRequest(mesage: String): Future[HttpResponse] = Future.successful(HttpResponse(
    status = 400,
    entity = HttpEntity(
      ContentTypes.`application/json`,
      "{\"message\": \"%s\"}".format(mesage)
    )
  ))

  def getRoutes(request: HttpRequest): Future[HttpResponse] = List[HttpRequest => Future[HttpResponse]](
    games,
    forecasts,
    score,
    notFound
  )
    .map(_.apply(request))
    .filter(_ != null)
    .head
}
