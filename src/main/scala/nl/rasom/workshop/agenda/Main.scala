package nl.rasom.workshop.agenda

import nl.rasom.workshop.agenda.console.AgendaZioCli
import nl.rasom.workshop.agenda.console.AgendaZioCli._
import nl.rasom.workshop.agenda.repository.AgendaServiceSQLite
import zio.cli.HelpDoc.Span.text
import zio.cli._
import zio.{Scope, ZIOAppArgs}
import nl.rasom.workshop.agenda.service.AgendaService
import com.typesafe.config.ConfigFactory
import com.typesafe.config.Config
import os.Path
import os.PermSet
import os.RelPath

object Main extends ZIOCliDefault {

  val config: Config = ConfigFactory.load();
  val dbFileRelativePath = config.getString("sqlite.dbFileRelativePath")

  val agendaService: AgendaService = AgendaServiceSQLite(dbFileRelativePath)

  val cliApp = AgendaZioCli.make(agendaService)

}
