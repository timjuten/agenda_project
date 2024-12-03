package nl.rasom.workshop.agenda.domain

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
