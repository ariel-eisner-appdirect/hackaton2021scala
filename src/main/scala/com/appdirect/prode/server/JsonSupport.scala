package com.appdirect.prode.server

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpEntity, HttpRequest}
import com.appdirect.prode.service.{Forecast, ForecastList, Game, GameList, Score, ScoreList}
import spray.json.{DefaultJsonProtocol, JsonParser, RootJsonFormat}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val gameJson: RootJsonFormat[Game] = jsonFormat5(Game)
  implicit val gameList: RootJsonFormat[GameList] = jsonFormat1(GameList)
  implicit val forecastJson: RootJsonFormat[Forecast] = jsonFormat2(Forecast)
  implicit val forecastList: RootJsonFormat[ForecastList] = jsonFormat1(ForecastList)
  implicit val scoreJson: RootJsonFormat[Score] = jsonFormat2(Score)
  implicit val scoreList: RootJsonFormat[ScoreList] = jsonFormat1(ScoreList)

  def toJson[T](request: HttpRequest, formatter: RootJsonFormat[T]): T =
    formatter.read(JsonParser.apply(request.entity.asInstanceOf[HttpEntity.Strict].data.utf8String))
}