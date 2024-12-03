package nl.rasom.workshop.agenda.domain

import java.time.LocalDate

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
