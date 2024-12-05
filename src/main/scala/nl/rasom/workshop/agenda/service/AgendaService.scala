package nl.rasom.workshop.agenda.service

import nl.rasom.workshop.agenda.domain.Status
import nl.rasom.workshop.agenda.domain.Task

import java.sql.Connection
import java.sql.Date
import java.sql.DriverManager
import java.sql.ResultSet
import java.time.LocalDate
import java.util.UUID

trait AgendaService {
  def add(task: Task): Unit
  def show(): List[Task]
  def remove(id: Int): Unit
  def finish(id: Int): Unit
}
