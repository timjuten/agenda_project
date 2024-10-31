import zio.Console.printLine
import zio.cli.HelpDoc.Span.text
import zio.cli._

import java.time.LocalDate

object Main extends ZIOCliDefault {

  sealed trait Subcommand extends Product with Serializable
  object Subcommand {
    final case class Add(date: LocalDate, text: List[String]) extends Subcommand
  }

  val dateOptions: Options[LocalDate] = Options.localDate("d").alias("date")
  val addHelp: HelpDoc = HelpDoc.p("Add subcommand description")
  val add =
    Command("add", dateOptions, Args.Variadic(Args.text("text"), Some(1),None))
      .withHelp(addHelp)
      .map { case (date, text) =>
        Subcommand.Add(date = date, text = text)
      }

  val agenda: Command[Subcommand] =
    Command("agenda", Options.none, Args.none).subcommands(add)

  val cliApp = CliApp.make(
    name = "Agenda",
    version = "0.0.1",
    summary = text("The best agenda console tool instument"),
    command = agenda
  ) { case Subcommand.Add(date, text) =>
    printLine(
      s"Executing `agenda add $date` ${text.mkString(" ")}"
    )
 
  }
}