import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.rtfmarket.http.{MainRoute, ProductHttp}
import com.rtfmarket.services.ProductServiceImpl
import akka.http.scaladsl.server.Directives._
import com.rtfmarket.slick._
import com.rtfmarket.slick.Database
import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.io.StdIn
import scala.concurrent.duration._
import scala.reflect
import scala.reflect.api

object Main extends App {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val db = Database.forConfig("rtfm")

  val category = CategoryRow(
    id = CategoryId.Test,
    name = "clothes",
    slug = "stuff",
    title = "amazing",
    description = "not kidding"
  )

  val createCategories = Categories.schema.create
  val createProducts = Products.schema.create
  val createFilters = Filters.schema.create

  val insert = Categories += category
  val future = db run (
    createCategories andThen
      createProducts andThen
      createFilters andThen
      insert)

  val result = Await.result(future, 2.seconds)

  val productDb = new ProductServiceImpl.Db(db)
  val productService = new ProductServiceImpl(productDb)
  val productHttp = new ProductHttp(productService)

  val route = MainRoute.route ~ productHttp.route

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8081)

  println(s"Server online at http://localhost:8081/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ ⇒ system.terminate())
}
