package nl.rasom.workshop.agenda.domain

trait Status

object Status {

  case object New extends Status
  case object Done extends Status

  def fromString(str: String): Option[Status] = str match {
    case "New"  => Some(New)
    case "Done" => Some(Done)
    case _      => None
  }
}
