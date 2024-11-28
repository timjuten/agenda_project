package agenda
import java.time.LocalDate
import AgendaService.Task
import java.util.UUID
import java.sql.{Connection, DriverManager, ResultSet}
import java.sql.Date
import agenda.AgendaService.Status
// lihaoyi https://github.com/com-lihaoyi/scalasql/blob/main/scalasql/test/src/example/SqliteExample.scala

trait AgendaService {
  def add(date: LocalDate, text: String): Unit
  def show(): List[Task]
}

object AgendaService {

  trait Status
  object Status {
    case object New extends Status
    case object Done extends Status
    def fromString(str: String): Status = str match {
      case "New"  => New
      case "Done" => Done
      case nonValid =>
        throw new RuntimeException(s"Not supported status value: $nonValid")
    }
  }

  case class Task(
      id: Option[Int],
      status: Status,
      date: LocalDate,
      text: String
  )

  object Task {
    def apply(date: LocalDate, text: String): Task = {
      Task(id = None, status = Status.New, date = date, text = text)
    }
  }
}
