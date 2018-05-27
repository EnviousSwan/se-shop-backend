import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.rtfmarket.http.{MainRoute, ProductHttp}
import com.rtfmarket.services.ProductServiceImpl
import akka.http.scaladsl.server.Directives._
import com.rtfmarket.slick.Database

import scala.io.StdIn

object Main extends App {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val db = Database.forConfig("rtfm")
  val productService = new ProductServiceImpl(db)
  val productHttp = new ProductHttp(productService)

  val route = MainRoute.route ~ productHttp.route

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8081)

  println(s"Server online at http://localhost:8081/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ ⇒ system.terminate())
}
