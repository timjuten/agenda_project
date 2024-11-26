package logic
import java.time.LocalDate
import Logic.Task
import java.util.UUID
import java.sql.{Connection, DriverManager, ResultSet}
import java.sql.Date
import logic.Logic.Status
// lihaoyi https://github.com/com-lihaoyi/scalasql/blob/main/scalasql/test/src/example/SqliteExample.scala

trait Logic {
  def add(date: LocalDate, text: String): Unit
  def show(): List[Task]
}

object DefaultLogic extends Logic {
  val dbUrl = "jdbc:sqlite:data.db"
  // Establish a connection to SQLite
  def connect(): Connection =
    try {
      DriverManager.getConnection(dbUrl)
    } finally {
      println("finally")
    }

  def initDatabase(): Unit = {
    println("Database connect!")
    val conn = connect()
    println("Create statement")
    try {
      val statement = conn.createStatement()
      val createTableSQL =
        """CREATE TABLE IF NOT EXISTS tasks (
          |  id INTEGER PRIMARY KEY AUTOINCREMENT,
          |  status TEXT NOT NULL,
          |  date DATETIME NOT NULL,
          |  text TEXT NOT NULL
          |);""".stripMargin
      println("Before execution")
      statement.execute(createTableSQL)
      println("Database initialized!")
    } finally {
      conn.close()
    }
  }
  var tasks: List[Task] = List()

  override def add(date: LocalDate, text: String): Unit = {
    initDatabase()

    val newTask = Task(date, text)
    insertUser(newTask)
    // tasks = newTask :: tasks
    // println(s"add new task: $newTask")
    // println(s"add tasks: $tasks")
    // newTask.id
  }
  private def insertUser(task: Task): Unit = {
    val conn = connect()
    try {
      val sql = "INSERT INTO tasks (status, date, text) VALUES (?, ?, ?)"
      val pstmt = conn.prepareStatement(sql)
      pstmt.setString(1, task.status.toString())
      pstmt.setDate(2, Date.valueOf(task.date))
      pstmt.setString(3, task.text)
      pstmt.executeUpdate()
      println(s"User ${task.text} inserted!")
    } finally {
      conn.close()
    }
  }
  implicit class ResultSetOps(resultSet: ResultSet) {
    def map[T](f: ResultSet => T): Iterator[T] = {
      Iterator.unfold(resultSet.next()) { hasNext =>
        Option.when(hasNext)(f(resultSet), resultSet.next())
      }
    }
  }
  private def showTasks(): List[Task] = {
    val conn = connect()
    try {
      val sql = "SELECT * FROM tasks"
      val pstmt = conn.prepareStatement(sql)
      // pstmt.setString(1, task.status.toString())
      // pstmt.setDate(2, Date.valueOf(task.date))
      // pstmt.setString(2, task.text)
      val res: ResultSet = pstmt.executeQuery()
      val tasks = res.map(rs => {
        println(s"working on $rs")
        val task = Task(
          id = rs.getInt("id"),
          status = Status.fromString(rs.getString("status")),
          date = rs.getDate("date").toLocalDate(),
          text = rs.getString("text")
        )
        println(s"this is a task: $task")
        task
      })

      println(s"iterator ${tasks.hasNext}")
      // println(s"User ${tasks.foreach(_.toString())} queried!")
      tasks.toList

    } finally {
      conn.close()
    }
  }
  override def show(): List[Task] = {
    showTasks()
    // println(s"show tasks: $tasks")
    // tasks
  }

}

object Logic {

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

  case class Task(id: Int, status: Status, date: LocalDate, text: String)
  object Task {
    var id = 0
    def apply(date: LocalDate, text: String): Task = {
      id = id + 1
      Task(id = id, status = Status.New, date = date, text = text)
    }
  }
}
