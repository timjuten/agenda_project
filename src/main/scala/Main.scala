import zio.Console.printLine
import zio.cli.HelpDoc.Span.text
import zio.cli._

import java.nio.file.{Path => JPath}

object Main extends ZIOCliDefault {
  import java.nio.file.Path

  sealed trait Subcommand extends Product with Serializable
  object Subcommand {
    final case class Add(modified: Boolean, directory: JPath) extends Subcommand
    // final case class Remote(verbose: Boolean) extends Subcommand
    // object Remote {
    //   sealed trait RemoteSubcommand extends Subcommand
    //   final case class Add(name: String, url: String) extends RemoteSubcommand
    //   final case class Remove(name: String) extends RemoteSubcommand
    // }
  }

  val modifiedFlag: Options[Boolean] = Options.boolean("m")

  val addHelp: HelpDoc = HelpDoc.p("Add subcommand description")
  val add =
    Command("add", modifiedFlag, Args.directory("directory"))
      .withHelp(addHelp)
      .map { case (modified, directory) =>
        Subcommand.Add(modified, directory)
      }

  // val verboseFlag: Options[Boolean] = Options.boolean("verbose").alias("v")
  // val configPath: Options[Path] = Options.directory("c", Exists.Yes)

  // val remoteAdd = {
  //   val remoteAddHelp: HelpDoc = HelpDoc.p("Add remote subcommand description")
  //   Command("add", Options.text("name") ++ Options.text("url"))
  //     .withHelp(remoteAddHelp)
  //     .map { case (name, url) =>
  //       Subcommand.Remote.Add(name, url)
  //     }
  // }

  // val remoteRemove = {
  //   val remoteRemoveHelp: HelpDoc =
  //     HelpDoc.p("Remove remote subcommand description")
  //   Command("remove", Args.text("name"))
  //     .withHelp(remoteRemoveHelp)
  //     .map(Subcommand.Remote.Remove)
  // }

  // val remoteHelp: HelpDoc = HelpDoc.p("Remote subcommand description")
  // val remote = {
  //   Command("remote", verboseFlag)
  //     .withHelp(remoteHelp)
  //     .map(Subcommand.Remote(_))
  //     .subcommands(remoteAdd, remoteRemove)
  //     .map(_._2)
  // }

  val agenda: Command[Subcommand] =
    Command("agenda", Options.none, Args.none).subcommands(add) // , remote)

  val cliApp = CliApp.make(
    name = "Git Version Control",
    version = "0.9.2",
    summary = text("a client for the git dvcs protocol"),
    command = agenda
  ) { case Subcommand.Add(modified, directory) =>
    printLine(
      s"Executing `agenda add $directory` with modified flag set to $modified"
    )
  // case Subcommand.Remote.Add(name, url) =>
  //   printLine(s"Executing `git remote add $name $url`")
  // case Subcommand.Remote.Remove(name) =>
  //   printLine(s"Executing `git remote remove $name`")
  // case Subcommand.Remote(verbose) =>
  //   printLine(s"Executing `git remote` with verbose flag set to $verbose")

  }
}