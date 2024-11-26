package logic
import java.time.LocalDate
import Logic.Task
import java.util.UUID
import java.sql.{Connection, DriverManager, ResultSet}
import java.sql.Date
trait Logic {
  def add(date: LocalDate, text: String): Int
  def show(): List[Task]
}

object DefaultLogic extends Logic {
  val dbUrl = "jdbc:sqlite:data.db"
  // Establish a connection to SQLite
  def connect(): Connection = DriverManager.getConnection(dbUrl)
  def initDatabase(): Unit = {
    val conn = connect()
    try {
      val statement = conn.createStatement()
      val createTableSQL =
        """CREATE TABLE IF NOT EXISTS users (
          |  id INTEGER PRIMARY KEY AUTOINCREMENT,
          |  status TEXT NOT NULL,
          |  date DATETIME NOT NULL,
          |  text TEXT NOT NULL
          |);""".stripMargin
      statement.execute(createTableSQL)
      println("Database initialized!")
    } finally {
      conn.close()
    }
  }
  var tasks: List[Task] = List()

  override def add(date: LocalDate, text: String): Int = {
    initDatabase()

    val newTask = Task(date, text)
    tasks = newTask :: tasks
    println(s"add new task: $newTask")
    println(s"add tasks: $tasks")
    newTask.id
  }
  private def insertUser(task: Task): Unit = {
    val conn = connect()
    try {
      val sql = "INSERT INTO tasks (status, date, task) VALUES (?, ?)"
      val pstmt = conn.prepareStatement(sql)
      pstmt.setString(1, task.status.toString())
      pstmt.setDate(2, Date.valueOf(task.date))
      pstmt.setString(2, task.text)
      pstmt.executeUpdate()
      println(s"User ${task.text} inserted!")
    } finally {
      conn.close()
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
      val res = pstmt.executeQuery()
      println(s"User ${res} queried!")
      List()

    } finally {
      conn.close()
    }
  }
  override def show(): List[Task] = {
    println(s"show tasks: $tasks")
    showTasks()
    tasks
  }

}

object Logic {

  trait Status
  object Status {
    case object New extends Status
    case object Done extends Status
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
