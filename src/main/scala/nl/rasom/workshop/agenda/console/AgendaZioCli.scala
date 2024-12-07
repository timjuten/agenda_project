package nl.rasom.workshop.agenda.console

import java.io.IOException
import java.time.LocalDate

import nl.rasom.workshop.agenda.console.render.ConsoleTable
import nl.rasom.workshop.agenda.domain.Task
import nl.rasom.workshop.agenda.service.AgendaService
import zio.Console.printLine
import zio.cli.HelpDoc.Span.text
import zio.cli._
import zio.{IO, Scope, ZIO, ZIOAppArgs}

object AgendaZioCli {

  val dateOptions: Options[LocalDate] = Options.localDate("d").alias("date")
  val addHelp: HelpDoc = HelpDoc.p("Add subcommand description")
  val add = Command("add", dateOptions, Args.Variadic(Args.text("text"), Some(1), None))
    .withHelp(addHelp)
    .map { case (date, text) =>
      Subcommand.Add(date = date, text = text)
    }

  val indexOptions: Options[BigInt] = Options.integer("i")
  val finishHelp: HelpDoc = HelpDoc.p("Mark task as done")
  val finish = Command("finish", indexOptions, Args.none)
    .withHelp(finishHelp)
    .map { case (id) =>
      Subcommand.Finish(id = id)
    }
  val removeHelp: HelpDoc = HelpDoc.p("The task has been deleted")
  val remove = Command("remove", indexOptions, Args.none)
    .withHelp(removeHelp)
    .map { case (id) =>
      Subcommand.Remove(id = id)
    }

  val showHelp: HelpDoc = HelpDoc.p("Show list of tasks")
  val show = Command("show", Options.none, Args.none)
    .withHelp(showHelp)
    .map { case _ => Subcommand.Show }

  val agenda = Command("agenda", Options.none, Args.none)
    .subcommands(
      add,
      show,
      finish,
      remove
    )

  private def logic(
      agendaService: AgendaService
  ): Subcommand => IO[IOException, Unit] = (subcommand: Subcommand) =>
    subcommand match {
      case Subcommand.Add(date, text) =>
        ZIO.succeed(
          agendaService.add(Task(date = date, text = text.mkString(" ")))
        )
      case Subcommand.Show =>
        executeShow(agendaService)
      case Subcommand.Remove(id) =>
        for {
          _ <- ZIO.succeed(agendaService.remove(id.intValue))
          _ <- printLine(s"Task with id=$id is removed")
          _ <- executeShow(agendaService)
        } yield ()
      case Subcommand.Finish(id) =>
        for {
          _ <- ZIO.succeed(agendaService.finish(id.intValue))
          _ <- printLine(s"Task with id=$id is finished")
        } yield ()
      case cmd => printLine(s"Unknown subcommand: $cmd")
    }

  def make(
      agendaService: AgendaService
  ): CliApp[Any with ZIOAppArgs with Scope, Any, Subcommand] = CliApp.make(
    name = "Agenda",
    version = "0.0.1",
    summary = text("The best agenda console tool instument"),
    command = agenda
  ) { logic(agendaService) }

  private def executeShow(agendaService: AgendaService) =
    for {
      tasks <- ZIO.succeed(agendaService.show())
      _ <- printLine(ConsoleTable.drawTable(entities = tasks.sortBy(_.date), emptyListMessage = "NO TASKS"))

    } yield ()
}
