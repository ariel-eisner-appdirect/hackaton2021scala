package com.appdirect.prode

package object service {
  case class GameList(data: List[Game])
  case class ForecastList(data: List[Forecast])
  case class ScoreList(data: List[Score])

  // Game
  final case class Game(instance: String, teamA: String, teamB: String, goalsA: Int, goalsB: Int) {

    override def equals(obj: Any): Boolean = obj match {
      case g: Game => instance == g.instance && teamA == g.teamA && teamB == g.teamB
      case _ => false
    }

    override def toString: String = s"Instance $instance - $teamA scored $goalsA - $teamB scored $goalsB"
  }

  // Forecast
  final case class Forecast(username: String, game: Game) {

    override def equals(obj: Any): Boolean = obj match {
      case f: Forecast => game.equals(f.game) && username.equals(f.username)
      case _ => false
    }

    override def toString: String = s"$username forecasted: $game"
  }

  // Score
  final case class Score(username: String, score: Int = 0) {
    override def toString: String = s"$username score: $score"
  }
}