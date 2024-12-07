package nl.rasom.workshop.agenda.repository

import java.time.LocalDate

import nl.rasom.workshop.agenda.domain.{Status, Task}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import os.{Path, SubPath}

class AgendaServiceSQLiteSpec extends AnyWordSpecLike with Matchers {

  trait Scope {

    val testTempDir: Path = os.temp.dir(
      dir = os.root / "tmp",
      prefix = "agenda",
      deleteOnExit = true,
      perms = Integer.parseInt("700", 8)
    )

    val dbFileSubPath: SubPath = SubPath("agenda/agenda.db")
    val dbFilePath: Path = testTempDir / dbFileSubPath

    val agendaService = AgendaServiceSQLite(dbFilePath)
  }

  "AgendaServiceSQLite" should {

    "initialize and create db file" in new Scope {
      os.exists(dbFilePath) shouldBe true
    }

    "add new task and immediately read it" in new Scope {
      val date = LocalDate.now()
      val text = "simple"

      agendaService.add(Task(date, text))

      val results = agendaService.show()
      results.headOption shouldEqual Some(
        Task(
          id = Some(1),
          status = Status.New,
          date = date,
          text = text
        )
      )
    }

    "mark task as done" in new Scope {
      val date = LocalDate.now()
      val text = "simple"

      agendaService.add(Task(date, text))
      agendaService.finish(id = 1)

      val results = agendaService.show()
      results.headOption shouldEqual Some(
        Task(
          id = Some(1),
          status = Status.Done,
          date = date,
          text = text
        )
      )
    }

    "remove task" in new Scope {
      val date = LocalDate.now()
      val text = "simple"

      agendaService.add(Task(date, text))
      agendaService.remove(id = 1)

      val results = agendaService.show()
      results.size shouldBe 0
    }
  }
}
