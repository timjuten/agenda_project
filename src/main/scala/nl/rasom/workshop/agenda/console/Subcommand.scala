package nl.rasom.workshop.agenda.console

import java.time.LocalDate

sealed trait Subcommand extends Product with Serializable
object Subcommand {
  final case class Add(date: LocalDate, text: List[String]) extends Subcommand
  final case class Finish(ids: List[BigInt]) extends Subcommand
  final case class Remove(ids: List[BigInt]) extends Subcommand
  final case class Show(all: Boolean) extends Subcommand
}
