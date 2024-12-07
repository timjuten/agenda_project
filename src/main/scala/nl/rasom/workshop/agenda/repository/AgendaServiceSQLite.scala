package nl.rasom.workshop.agenda.repository

import java.sql.{Connection, Date, DriverManager, ResultSet}

import scala.util.Try

import nl.rasom.workshop.agenda.domain.{Status, Task}
import nl.rasom.workshop.agenda.repository.AgendaServiceSQLite.{
  connect,
  executeFinishStatement,
  executeInsertStatement,
  executeRemoveStatement,
  executeSelectQuery
}
import nl.rasom.workshop.agenda.service.AgendaService
import os.Path

// lihaoyi https://github.com/com-lihaoyi/scalasql/blob/main/scalasql/test/src/example/SqliteExample.scala

class AgendaServiceSQLite private (dbUrl: String) extends AgendaService {

  override def add(task: Task): Unit =
    (for {
      conn <- connect(dbUrl)
      _ = executeInsertStatement(conn, task)
    } yield {
      conn.close()
    }).getOrElse(())

  override def show(): List[Task] =
    (for {
      conn <- connect(dbUrl)
      tasks = executeSelectQuery(conn)
    } yield {
      val res = tasks.toList
      conn.close()
      res
    }).getOrElse(List.empty[Task])

  override def remove(id: Int): Unit =
    for {
      conn <- connect(dbUrl)
      _ = executeRemoveStatement(conn, id)
    } yield {
      conn.close()
    }

  override def finish(id: Int): Unit =
    for {
      conn <- connect(dbUrl)
      _ = executeFinishStatement(conn, id)
    } yield {
      conn.close()
    }

}

object AgendaServiceSQLite {

  def apply(dbFilePath: Path): AgendaServiceSQLite = {
    prepareDbFile(dbFilePath)
    val dbUrl: String = "jdbc:sqlite:" + dbFilePath
    initDatabase(dbUrl)
    new AgendaServiceSQLite(dbUrl)
  }

  private def prepareDbFile(dbFilePath: Path): Unit = {
    if (!os.exists(dbFilePath / os.up))
      os.makeDir.all(
        path = dbFilePath / os.up,
        perms = Integer.parseInt("700", 8)
      )
    if (!os.exists(dbFilePath))
      os.write(
        target = dbFilePath,
        data = "",
        perms = Integer.parseInt("600", 8)
      )
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
        status = Status.fromString(rs.getString("status")).getOrElse(throw new RuntimeException("Can't read status from db")),
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

  private def executeRemoveStatement(conn: Connection, id: Int): Int = {
    val sql = "DELETE FROM tasks WHERE id = ?"
    val pstmt = conn.prepareStatement(sql)
    pstmt.setInt(1, id)
    pstmt.executeUpdate()
  }

  private def executeFinishStatement(conn: Connection, id: Int): Int = {
    val sql = "UPDATE tasks SET status = ? WHERE id = ?"
    val pstmt = conn.prepareStatement(sql)
    pstmt.setString(1, Status.Done.toString())
    pstmt.setInt(2, id)
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
