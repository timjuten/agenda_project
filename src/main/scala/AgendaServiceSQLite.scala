package agenda

import java.time.LocalDate
import AgendaService.Task
import java.util.UUID
import java.sql.{Connection, DriverManager, ResultSet}
import java.sql.Date
import AgendaService.Status
import scala.util.Try
// lihaoyi https://github.com/com-lihaoyi/scalasql/blob/main/scalasql/test/src/example/SqliteExample.scala

object AgendaServiceSQLite extends AgendaService {

  val dbUrl = "jdbc:sqlite:data.db"

  override def add(date: LocalDate, text: String): Unit = {
    initDatabase()
    insertTask(Task(date, text))
  }

  override def show(): List[Task] = getTasks()

  private def initDatabase(): Unit = {
    for {
      conn <- connect()
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

  private def insertTask(task: Task): Unit = {
    (for {
      conn <- connect()
      _ = executeInsertStatement(conn, task)
    } yield {
      conn.close()
    }).getOrElse(())
  }

  private def getTasks(): List[Task] = {
    (for {
      conn <- connect()
      tasks = executeSelectQuery(conn)
    } yield {
      val res = tasks.toList
      conn.close()
      res
    }).getOrElse(List.empty[Task])
  }

  private def connect(): Try[Connection] = Try(
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
