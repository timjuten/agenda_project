package nl.rasom.workshop.agenda.service

import java.time.LocalDate
import nl.rasom.workshop.agenda.domain.{Task, Status}
import java.util.UUID
import java.sql.{Connection, DriverManager, ResultSet}
import java.sql.Date

trait AgendaService {
  def add(task: Task): Unit
  def show(): List[Task]
}
