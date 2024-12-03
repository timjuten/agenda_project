package nl.rasom.workshop.agenda.repository

import nl.rasom.workshop.agenda.domain.Status
import nl.rasom.workshop.agenda.domain.Task
import nl.rasom.workshop.agenda.service.AgendaService

import java.net.URLEncoder
import java.sql.Connection
import java.sql.Date
import java.sql.DriverManager
import java.sql.ResultSet
import java.time.LocalDate
import java.util.UUID
import scala.util.Try
import nl.rasom.workshop.agenda.repository.AgendaServiceSQLite.connect
import nl.rasom.workshop.agenda.repository.AgendaServiceSQLite.executeInsertStatement
import nl.rasom.workshop.agenda.repository.AgendaServiceSQLite.executeSelectQuery
import os.Path
import os.RelPath

// lihaoyi https://github.com/com-lihaoyi/scalasql/blob/main/scalasql/test/src/example/SqliteExample.scala

class AgendaServiceSQLite private (dbUrl: String) extends AgendaService {

  override def add(task: Task): Unit = insertTask(task)

  override def show(): List[Task] = getTasks()

  private def insertTask(task: Task): Unit = {
    (for {
      conn <- connect(dbUrl)
      _ = executeInsertStatement(conn, task)
    } yield {
      conn.close()
    }).getOrElse(())
  }

  private def getTasks(): List[Task] = {
    (for {
      conn <- connect(dbUrl)
      tasks = executeSelectQuery(conn)
    } yield {
      val res = tasks.toList
      conn.close()
      res
    }).getOrElse(List.empty[Task])
  }

}

object AgendaServiceSQLite {

  def apply(dbFileRelativePath: String): AgendaServiceSQLite = {
    val dbFilePath: String = prepareDbFile(dbFileRelativePath)
    val dbUrl: String = "jdbc:sqlite:" + dbFilePath
    initDatabase(dbUrl)
    new AgendaServiceSQLite(dbUrl)
  }

  private def prepareDbFile(dbFileRelativePath: String): String = {
    val dbFilePath: Path = os.home / RelPath(dbFileRelativePath)
    if (!os.exists(dbFilePath / os.up))
      os.makeDir.all(path = dbFilePath / os.up)
    if (!os.exists(dbFilePath))
      os.write(
        target = dbFilePath,
        data = "",
        perms = Integer.parseInt("666", 8)
      )
    dbFilePath.toString()
  }

  private def initDatabase(dbUrl: String): Unit = {
    for {
      conn <- connect(dbUrl)
      statement = conn.createStatement()
      createTableSQL =
        """CREATE TABLE IF NOT EXISTS tasks (
          |  id INTEGER PRIMARY KEY AUTOINCREMENT,
          |  status TEXT NOT NULL,
          |  date DATETIME NOT NULL,
          |  text TEXT NOT NULL
          |);""".stripMargin
      _ = statement.execute(createTableSQL)
    } yield {
      conn.close()
      ()
    }
  }

  private def connect(dbUrl: String): Try[Connection] = Try(
    DriverManager.getConnection(dbUrl)
  )

  private def executeSelectQuery(conn: Connection) = {
    val sql = "SELECT * FROM tasks"
    val pstmt = conn.prepareStatement(sql)
    val res: ResultSet = pstmt.executeQuery()
    res.map(rs => {
      Task(
        id = Some(rs.getInt("id")),
        status = Status.fromString(rs.getString("status")),
        date = rs.getDate("date").toLocalDate(),
        text = rs.getString("text")
      )
    })

  }

  private def executeInsertStatement(conn: Connection, task: Task) = {
    val sql = "INSERT INTO tasks (status, date, text) VALUES (?, ?, ?)"
    val pstmt = conn.prepareStatement(sql)
    pstmt.setString(1, task.status.toString())
    pstmt.setDate(2, Date.valueOf(task.date))
    pstmt.setString(3, task.text)
    pstmt.executeUpdate()

  }

  implicit class ResultSetOps(resultSet: ResultSet) {
    def map[T](f: ResultSet => T): Iterator[T] = {
      Iterator.unfold(resultSet.next()) { hasNext =>
        Option.when(hasNext)(f(resultSet), resultSet.next())
      }
    }
  }

}
