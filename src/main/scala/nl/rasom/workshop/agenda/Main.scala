package nl.rasom.workshop.agenda

import com.typesafe.config.{Config, ConfigFactory}
import nl.rasom.workshop.agenda.console.AgendaZioCli
import nl.rasom.workshop.agenda.repository.AgendaServiceSQLite
import nl.rasom.workshop.agenda.service.AgendaService
import os.{Path, SubPath}
import zio.cli.ZIOCliDefault

object Main extends ZIOCliDefault {

  val config: Config = ConfigFactory.load();
  val dbFileSubPath: String = config.getString("sqlite.dbFileRelativePath")
  val dbFilePath: Path = os.home / SubPath(dbFileSubPath)

  val agendaService: AgendaService = AgendaServiceSQLite(dbFilePath)

  val cliApp = AgendaZioCli.make(agendaService)

}
