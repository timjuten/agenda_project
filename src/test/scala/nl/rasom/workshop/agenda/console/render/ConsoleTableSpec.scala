package nl.rasom.workshop.agenda.console.render

import java.time.LocalDate

import nl.rasom.workshop.agenda.domain.{Status, Task}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class ConsoleTableSpec extends AnyWordSpecLike with Matchers {
  case class Testing(id: Option[Int], name: String, description: Option[String])
  "ConsoleTableSpec" should {
    "render empty table" in {
      val result = ConsoleTable.drawTable(List.empty, "empty message")
      result shouldBe "empty message"
    }
    "render one task" in {
      val tasks = List(Task(id = Some(1), status = Status.New, date = LocalDate.parse("2024-11-11"), text = "simple task"))

      val result = ConsoleTable.drawTable(tasks, "empty message")
      result shouldBe
        "------------------------------------------" + "\n" +
        "| id | status | date       | text        |" + "\n" +
        "|----|--------|------------|-------------|" + "\n" +
        "| 1  | New    | 2024-11-11 | simple task |" + "\n" +
        "------------------------------------------"
    }
    "render multiple task" in {
      val tasks = List(
        Task(id = Some(1), status = Status.New, date = LocalDate.parse("2024-11-11"), text = "simple task"),
        Task(id = Some(2), status = Status.Done, date = LocalDate.parse("2024-11-12"), text = "simple task 2"),
        Task(id = Some(3), status = Status.New, date = LocalDate.parse("2024-11-15"), text = "simple task with very long name 3")
      )

      val result = ConsoleTable.drawTable(tasks, "empty message")
      result shouldBe
        "----------------------------------------------------------------" + "\n" +
        "| id | status | date       | text                              |" + "\n" +
        "|----|--------|------------|-----------------------------------|" + "\n" +
        "| 1  | New    | 2024-11-11 | simple task                       |" + "\n" +
        "| 2  | Done   | 2024-11-12 | simple task 2                     |" + "\n" +
        "| 3  | New    | 2024-11-15 | simple task with very long name 3 |" + "\n" +
        "----------------------------------------------------------------"
    }
    "render any multiple case classes" in {
      val testing = List(
        Testing(id = Some(1), name = "Alice", description = Some("good")),
        Testing(id = None, name = "Bob", description = Some("bad")),
        Testing(id = Some(2), name = "Sam", description = None)
      )

      val result = ConsoleTable.drawTable(testing, "empty message")
      result shouldBe
        "----------------------------" + "\n" +
        "| id | name  | description |" + "\n" +
        "|----|-------|-------------|" + "\n" +
        "| 1  | Alice | good        |" + "\n" +
        "|    | Bob   | bad         |" + "\n" +
        "| 2  | Sam   |             |" + "\n" +
        "----------------------------"
    }
  }
}
