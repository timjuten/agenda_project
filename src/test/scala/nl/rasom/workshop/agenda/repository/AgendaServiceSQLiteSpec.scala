package nl.rasom.workshop.agenda.repository

import org.scalatest.wordspec.AnyWordSpecLike
import os.temp
import os.Source
import os.RelPath
import org.scalatest.matchers.should.Matchers
import nl.rasom.workshop.agenda.domain.Task
import java.time.LocalDate
import nl.rasom.workshop.agenda.domain.Status

class AgendaServiceSQLiteSpec extends AnyWordSpecLike with Matchers {
  trait Scope {

    val tempDirPath = os.root / "tmp"
    val dbFileDir = os.temp.dir(
      dir = tempDirPath,
      prefix = "agenda",
      deleteOnExit = true,
      perms = Integer.parseInt("700", 8)
    )

    val dbFilePath = dbFileDir / "agenda" / "agenda.db"
    val agendaService = AgendaServiceSQLite(dbFilePath)
  }

  "AgendaServiceSQLite" should {

    "initialize and create db file" in new Scope {
      os.exists(dbFilePath) shouldBe true
    }

    "add new task and immediately read it" in new Scope {
      val newValue = LocalDate.now()
      val newValue1 = "simple"
      agendaService.add(Task(date = newValue, text = newValue1))
      val results = agendaService.show()
      results.headOption shouldEqual Some(
        Task(
          id = Some(1),
          status = Status.New,
          date = newValue,
          text = newValue1
        )
      )
    }
  }
}
