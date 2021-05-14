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
    score + (if (score == 3) 1 else 0)

  def calculate(game: Game): Forecast => Score = {
    val gameWinner = calculateWinner(game)

    val calculations: List[Forecast => Int] = List(
      forecast => calculateWinnerScore(gameWinner, calculateWinner(forecast.game)),
      forecast => calculateGoalsScore(game.goalsA, forecast.game.goalsA),
      forecast => calculateGoalsScore(game.goalsB, forecast.game.goalsB)
    )
    forecast => Score(forecast.username, checkBonus(calculations.map(_.apply(forecast)).sum))
  }

  def calculateGameWinners(game: Game, forecasts: List[Forecast]): List[Score] = forecasts.map(calculate(game))

  def calculateWinners(games: List[Game], forecasts: List[Forecast]): List[Score] = {
    games.flatMap(game => calculateGameWinners(game, forecasts.filter(_.game == game)))
      .groupBy(_.username)
      .map(entry => new Score(entry._1, entry._2.map(_.score).sum))
      .toList
      .sortBy(_.score)
      .reverse
  }
}
