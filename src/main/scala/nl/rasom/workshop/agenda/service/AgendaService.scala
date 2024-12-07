package nl.rasom.workshop.agenda.service
import nl.rasom.workshop.agenda.domain.Task

trait AgendaService {
  def add(task: Task): Unit
  def show(): List[Task]
  def remove(id: Int): Unit
  def finish(id: Int): Unit
}
