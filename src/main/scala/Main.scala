import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.rtfmarket.http.MainRoute
import slick.jdbc.H2Profile.api.Database

object Main extends App {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val db = Database.forConfig("mydb")

  Http().bindAndHandle(MainRoute.route, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
}
