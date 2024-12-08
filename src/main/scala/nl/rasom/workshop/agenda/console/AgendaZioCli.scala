package nl.rasom.workshop.agenda.console

import java.time.LocalDate

import nl.rasom.workshop.agenda.console.render.ConsoleTable
import nl.rasom.workshop.agenda.domain.Task
import nl.rasom.workshop.agenda.service.AgendaService
import zio.Console.printLine
import zio.ZIO
import zio.cli.HelpDoc.Span.text
import zio.cli._
import nl.rasom.workshop.agenda.domain.Status

object AgendaZioCli {

  val dateOptions: Options[LocalDate] = Options.localDate("d").alias("date")
  val addHelp: HelpDoc = HelpDoc.p("Add new task")
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
  val removeHelp: HelpDoc = HelpDoc.p("Remove the task")
  val remove = Command("remove", indexOptions, Args.none)
    .withHelp(removeHelp)
    .map { case (id) =>
      Subcommand.Remove(id = id)
    }

  val showFilterOption: Options[Boolean] = Options.boolean("a").alias("all")
  val showHelp: HelpDoc = HelpDoc.p("Show list of tasks")
  val show = Command("show", showFilterOption, Args.none)
    .withHelp(showHelp)
    .map { case (a) => Subcommand.Show(a) }

  val agenda = Command("agenda", Options.none, Args.none)
    .subcommands(
      add,
      show,
      finish,
      remove
    )

  private def logic(
      agendaService: AgendaService
  ) = (subcommand: Subcommand) =>
    subcommand match {
      case Subcommand.Add(date, text) =>
        ZIO.succeed(
          agendaService.add(Task(date = date, text = text.mkString(" ")))
        )
      case Subcommand.Show(a) =>
        executeShow(agendaService, a)
      case Subcommand.Remove(id) =>
        for {
          _ <- ZIO.succeed(agendaService.remove(id.intValue))
          _ <- printLine(s"Task with id=$id is removed")
          _ <- executeShow(agendaService = agendaService, all = false)
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
  ) = CliApp.make(
    name = "Agenda",
    version = " 0.0.1",
    summary = text("The simple task tracking tool"),
    command = agenda
  ) { logic(agendaService) }

  private def executeShow(agendaService: AgendaService, all: Boolean) =
    for {
      tasks <- ZIO.succeed(agendaService.show())
      filteredTasks <- ZIO.succeed(
        if (all) tasks else tasks.filter(_.status != Status.Done)
      )
      _ <- printLine(ConsoleTable.drawTable(entities = filteredTasks.sortBy(_.date), emptyListMessage = "NO TASKS"))

    } yield ()
}
