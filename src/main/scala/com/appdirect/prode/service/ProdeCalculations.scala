package com.appdirect.prode.service

trait ProdeCalculations {

  /*** PURE FUNCTIONS ***/

  def calculateWinner(game: Game): String = game.goalsA.compare(game.goalsB) match {
    case 1 => game.teamA
    case 0 => "TIE"
    case -1 => game.teamB
  }

  def calculateGoalsScore(goals: Int, forecastGoals: Int): Int =
    if (goals == forecastGoals) 1 else 0

  def calculateWinnerScore(gameWinner: String, forecastWinner: String): Int =
    if (gameWinner == forecastWinner) 1 else 0

  def checkBonus(score: Int): Int =
    if (score == 3) 1 else 0

  def calculate(game: Game, calculations: () => List[(String, Game, Int, Forecast) => Int]): Forecast => Score = {
    Option(calculateWinner(game))
      .map(winner => {
        forecast: Forecast => {
          var points = 0
          calculations()
            .foreach(calculator => points += calculator(winner, game, points, forecast))
          Score(forecast.username, points)
        }
      })
      .getOrElse(forecast => Score(forecast.username))
  }

  private def getCalculations(): List[(String, Game, Int, Forecast) => Int] = List[(String, Game, Int, Forecast) => Int](
    (gameWinner, _, _, forecast) => calculateWinnerScore(gameWinner, calculateWinner(forecast.game)),
    (_, game, _, forecast) => calculateGoalsScore(game.goalsA, forecast.game.goalsA),
    (_, game, _, forecast) => calculateGoalsScore(game.goalsB, forecast.game.goalsB),
    (_, _, points, _) => checkBonus(points)
  )

  def calculateGameWinners(game: Game, forecasts: List[Forecast]): List[Score] = {
    forecasts.map(calculate(game, getCalculations))
  }

  def calculateWinners(games: List[Game], forecasts: List[Forecast]): List[Score] = {
    games.flatMap(game => calculateGameWinners(game, forecasts.filter(_.game == game)))
      .groupBy(_.username)
      .map(entry => Score(entry._1, entry._2.map(_.score).sum))
      .toList
      .sortBy(_.score)
      .reverse
  }
}
