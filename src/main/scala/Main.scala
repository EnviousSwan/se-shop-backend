import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.rtfmarket.http.MainRoute
import slick.jdbc.H2Profile
import slick.jdbc.H2Profile.api.Database

import scala.io.StdIn

object Main extends App {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  implicit val ec = system.dispatcher

  val db: H2Profile.backend.Database = Database.forConfig("rtfm")

  val bindingFuture = Http().bindAndHandle(MainRoute.route, "localhost", 8081)

  println(s"Server online at http://localhost:8081/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ ⇒ system.terminate()) // and shutdown when done
}
