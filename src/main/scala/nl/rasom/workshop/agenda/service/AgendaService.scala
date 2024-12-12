package nl.rasom.workshop.agenda.service

import nl.rasom.workshop.agenda.domain.Task

trait AgendaService {
  def add(task: Task): Unit
  def show(): List[Task]
  def remove(ids: List[Int]): Unit
  def finish(ids: List[Int]): Unit
}
