package nl.rasom.workshop.agenda.console

import nl.rasom.workshop.agenda.domain.Task

object ConsoleTable {

  val header: (String, String, String, String) =
    ("id", "status", "date", "text")
  val headerLengths =
    (header._1.length, header._2.length, header._3.length, header._4.length)

  def maxColumnsTuple(tasks: List[Task]): (Int, Int, Int, Int) =
    tasks
      .map(t =>
        (
          t.id.map(_.toString().length()).getOrElse(0),
          t.status.toString().length(),
          t.date.toString().length(),
          t.text.toString().length()
        )
      )
      .foldLeft(headerLengths)((x, y) =>
        (
          Math.max(x._1, y._1),
          Math.max(x._2, y._2),
          Math.max(x._3, y._3),
          Math.max(x._4, y._4)
        )
      )

  def drawTable(tasks: List[Task]): String = {
    val maxColumns = maxColumnsTuple(tasks)
    showDashDelimeter(maxColumns) + "\n" +
      showHeaderInConsole(header, maxColumns) + "\n" +
      showLineDelimeter(maxColumns) + "\n" +
      tasks
        .sortBy(_.date)
        .map(showTaskInConsole(_, maxColumns))
        .mkString("\n") + "\n" +
      showDashDelimeter(maxColumns)
  }

  private def showHeaderInConsole(
      header: (String, String, String, String),
      maxColumns: (Int, Int, Int, Int)
  ): String = {
    val id = formatString(header._1, maxColumns._1)
    val status = formatString(header._2, maxColumns._2)
    val date = formatString(header._3, maxColumns._3)
    val text = formatString(header._4, maxColumns._4)
    s"| $id | $status | $date | $text |"
  }

  private def showLineDelimeter(maxColumns: (Int, Int, Int, Int)) =
    s"""| ${"-" * maxColumns._1} | ${"-" * maxColumns._2} | ${"-" * maxColumns._3} | ${"-" * maxColumns._4} |"""

  private def showDashDelimeter(maxColumns: (Int, Int, Int, Int)) = {
    val length = maxColumns._1 + maxColumns._2 + maxColumns._3 + maxColumns._4
    val columnFormattingLength = "|  |  |  |  |".length
    "-" * (length + columnFormattingLength)
  }

  private def showTaskInConsole(
      task: Task,
      maxColumns: (Int, Int, Int, Int)
  ): String = {
    val id =
      formatString(task.id.map(_.toString()).getOrElse(""), maxColumns._1)
    val status = formatString(task.status.toString, maxColumns._2)
    val date = formatString(task.date.toString, maxColumns._3)
    val text = formatString(task.text, maxColumns._4)
    s"| $id | $status | $date | $text |"
  }

  private def formatString(string: String, maxLength: Int) = {
    val diff = maxLength - string.length
    string + " " * diff
  }

}
