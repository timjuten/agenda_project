package nl.rasom.workshop.agenda.console.render

object ConsoleTable {

  private def maxColumnsTuple[T <: Product with Serializable](entities: List[T], header: List[String]): List[Int] =
    entities
      .map(entity =>
        entity.productIterator.toList.zipWithIndex.map(x => entity.productElement((x._2))).map {
          case Some(value) => value.toString().length()
          case None        => 0
          case value       => value.toString().length()
        }
      )
      .foldLeft(header.map(_.length()))((x, y) => x.zip(y).map(z => Math.max(z._1, z._2)))

  def drawTable[T <: Product with Serializable](entities: List[T], emptyListMessage: String): String = {
    entities.headOption match {
      case None => emptyListMessage
      case Some(entity) => {
        val header: List[String] = entity.productIterator.toList.zipWithIndex.map(x => entity.productElementName(x._2))
        val maxColumns = maxColumnsTuple(entities, header)
        showDashDelimeter(maxColumns) + "\n" +
          showHeaderInConsole(header, maxColumns) + "\n" +
          showLineDelimeter(maxColumns) + "\n" +
          entities
            .map(showEntityInConsole(_, maxColumns))
            .mkString("\n") + "\n" +
          showDashDelimeter(maxColumns)
      }
    }
  }

  private def showHeaderInConsole(
      header: List[String],
      maxColumns: List[Int]
  ): String = {
    val headerWithMaxColumns: List[(String, Int)] = header.zip(maxColumns)
    val innerString = headerWithMaxColumns.map(x => formatString(x._1, x._2)).mkString(" | ")
    s"| $innerString |"
  }

  private def showLineDelimeter(maxColumns: List[Int]) = {
    val innerString = maxColumns.map(x => "-" + ("-" * x) + "-").mkString("|")
    s"|$innerString|"
  }

  private def showDashDelimeter(maxColumns: List[Int]) = {
    val length = maxColumns.sum + maxColumns.length * 3 - 1
    val columnWrappingLength = "||".length
    "-" * (length + columnWrappingLength)
  }

  private def fieldToString(field: Any): String = field match {
    case Some(v) => v.toString()
    case None    => ""
    case v       => v.toString
  }
  private def showEntityInConsole[T <: Product with Serializable](
      entity: T,
      maxColumns: List[Int]
  ): String = {
    val entityFieldStrings = entity.productIterator.map(fieldToString).toList
    val entityStrings: List[(String, Int)] = entityFieldStrings.zip(maxColumns)
    val innerString = entityStrings.map(x => formatString(x._1, x._2)).mkString(" | ")

    s"| $innerString |"
  }

  private def formatString(string: String, maxLength: Int) = {
    val diff = maxLength - string.length
    string + " " * diff
  }

}
