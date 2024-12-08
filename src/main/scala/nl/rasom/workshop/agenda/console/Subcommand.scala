package nl.rasom.workshop.agenda.console

import java.time.LocalDate

sealed trait Subcommand extends Product with Serializable
object Subcommand {
  final case class Add(date: LocalDate, text: List[String]) extends Subcommand
  final case class Finish(id: BigInt) extends Subcommand
  final case class Remove(id: BigInt) extends Subcommand
  final case class Show(all: Boolean) extends Subcommand
}
