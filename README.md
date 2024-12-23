# Topics

1. Setup the simple zio-cli project
2. Setup the project using `sbt-assembly`
3. Prepare executable
4. Install gum

## 1. Setup the simple zio-cli project

- Create simple application
- Add zio and zio-cli dependencies:
Change `project/Dependencies.scala` file
```scala
import sbt._

object Dependencies {
  lazy val Zio = "dev.zio" %% "zio" % "2.0.0"
  lazy val ZioCli = "dev.zio" %% "zio-cli" % "0.7.0"
}
```
Change `build.sbt` file
```scala
import Dependencies._

// ... some code here

lazy val agenda = (project in file("."))
  .settings(
    name := "agenda",
    libraryDependencies += Zio,
    libraryDependencies += ZioCli,
  )
```
- follow the instruction: [ziocli documentation](https://zio.dev/zio-cli/):
 - extend ZIOCliDefault
 - Create Command
 - Create Subcommand
 - Add subcommand
 - Handle subcommand
 
## 2. Setup the project using `sbt-assembly`

- Dependency for sbt-assembly
Add the following code to `project/plugins.sbt`
```scala
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "2.3.0")
```
- Import Build

## 3. Prepare executable
- Execute sbt
```sh
sbt assembly
```
- Set executable permissions
Find `.jar` file in the `target/scala-2.13` directory
```sh
chmod +x agenda-assembly-0.1.1-SNAPSHOT.jar
sudo mv target/scala-2.13/agenda-assembly-0.1.1-SNAPSHOT.jar /usr/bin/agenda.jar
```
- Create wrapper file `/usr/bin/agenda` folder with content:
```script
#!/bin/bash
java -jar /usr/bin/agenda.jar $@
```
```sh
sudo chmod +x /usr/bin/agenda
which agenda 
```

## 4. Install gum
```sh
sudo mkdir -p /etc/apt/keyrings
curl -fsSL https://repo.charm.sh/apt/gpg.key | sudo gpg --dearmor -o /etc/apt/keyrings/charm.gpg
echo "deb [signed-by=/etc/apt/keyrings/charm.gpg] https://repo.charm.sh/apt/ * *" | sudo tee /etc/apt/sources.list.d/charm.list
sudo apt update && sudo apt install gum
```
```sh
sudo cp ~/projects/agenda_project/agendagum /usr/bin/
which agendagum
agendagum
```
