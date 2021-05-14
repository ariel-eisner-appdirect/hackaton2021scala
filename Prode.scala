import scala.language.implicitConversions

object Prode extends App {

  /** * SIDE EFFECTS ** */
  class Game(val instance: String, val teamA: String, val teamB: String, var goalsA: Int = 0, var goalsB: Int = 0) {

    def setResult(goalsA: Int, goalsB: Int) {
      this.goalsA = goalsA
      this.goalsB = goalsB
    }

    override def equals(obj: Any): Boolean = {
      obj match {
        case g: Game => instance == g.instance && teamA == g.teamA && teamB == g.teamB
        case _ => false
      }
    }

    override def toString: String = {
      "Instance %s - %s scored %d - %s scored %d".format(instance, teamA, goalsA, teamB, goalsB)
    }
  }

  class Forecast(val userName: String, val gameForecast: Game) {
    override def toString: String = {
      "%s forecasted: %s".format(userName, gameForecast)
    }
  }

  class Score(val userName: String, var score: Int = 0) {
    override def toString: String = {
      "%s score: %d".format(userName, score)
    }
  }

  var forecasts: List[Forecast] = List()
  var games: List[Game] = List()
  /** * OPERATIONS ** */

  // Create game
  def createGame = (newGame: Game) => games ++= List(newGame)

  // Create forecasts
  def createForecast = (newForecast: Forecast) => forecasts ++= List(newForecast)

  // Set game result
  def setGameResult = (gameToUpdate: Game, goalsA: Int, goalsB: Int) => gameToUpdate.setResult(goalsA, goalsB)

  def calculateWinner(game: Game): String = game.goalsA.compare(game.goalsB) match {
    case 1 => game.teamA
    case 0 => "TIE"
    case -1 => game.teamB
  }

  def calculateGoalsScore(goals: Int, forecastGoals: Int): Int = if (goals == forecastGoals) 1 else 0

  def calculateWinnerScore(gameWinner: String, forecastWinner: String): Int = if (gameWinner == forecastWinner) 1 else 0

  def checkBonus(score: Int): Int = score + (if (score == 3) 1 else 0)

  def calculate(game: Game): Forecast => Score = {
    val gameWinner = calculateWinner(game)

    val calculations: List[Forecast => Int] = List(
      forecast => calculateWinnerScore(gameWinner, calculateWinner(forecast.gameForecast)),
      forecast => calculateGoalsScore(game.goalsA, forecast.gameForecast.goalsA),
      forecast => calculateGoalsScore(game.goalsB, forecast.gameForecast.goalsB)
    )

    forecast => new Score(forecast.userName, checkBonus(calculations.map(_.apply(forecast)).sum))
  }

  def calculateGameWinners(game: Game, forecasts: List[Forecast]): List[Score] = forecasts.map(calculate(game))

  def calculateWinners(games: List[Game], forecasts: List[Forecast]): List[Score] = {
    games.flatMap(game =>
      calculateGameWinners(game, forecasts.filter(_.gameForecast == game))
    ).groupBy(_.userName)
      .map(entry => new Score(entry._1, entry._2.map(_.score).sum))
      .toList
      .sortBy(_.score)
      .reverse
  }

  createGame(new Game("round1","arg", "bra"))
  createGame(new Game("round1", "ven", "col"))

  createForecast(new Forecast("Pepino", new Game("round1", "arg", "bra", 1, 0)))
  createForecast(new Forecast("Pepino", new Game("round1", "ven", "col", 1, 0)))
  createForecast(new Forecast("Joaco", new Game("round1", "arg", "bra", 2, 0)))

  println(games)
  println(forecasts)
  setGameResult(games.head, 1, 0)
  setGameResult(games.last, 1, 4)
  println(games)
  println(calculateWinners(games, forecasts))
}
