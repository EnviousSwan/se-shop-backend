import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.LinkParams
import akka.http.scaladsl.model.headers.LinkParams.{media, title}
import akka.stream.ActorMaterializer
import com.rtfmarket.http.{MainRoute, ProductHttp, UserHttp}
import com.rtfmarket.services.{ProductServiceImpl, UserServiceImpl}
import akka.http.scaladsl.server.Directives._
import com.rtfmarket.slick._
import com.rtfmarket.slick.Database
import com.softwaremill.session.{DecodeResult, SessionConfig, SessionEncoder, SessionManager}
import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.io.StdIn
import scala.concurrent.duration._
import scala.util.Try

object Main extends App {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val db = Database.forConfig("rtfm")

  implicit val encoder = new SessionEncoder[UserId] {

    private val basic = SessionEncoder.basic[Long]

    def encode(t: UserId, nowMillis: Long, config: SessionConfig): String =
      basic.encode(t.value, nowMillis, config)

    override def decode(s: String, config: SessionConfig): Try[DecodeResult[UserId]] =
      basic.decode(s, config) map { x => x.copy(t = UserId(x.t))}
  }

  val sessionConfig = SessionConfig.default("some_very_long_secret_and_random_string_some_very_long_secret_and_random_string")
  implicit val sessionManager: SessionManager[UserId] = new SessionManager[UserId](sessionConfig)

  val category = CategoryRow(
    id = CategoryId.Test,
    name = "clothes",
    slug = "stuff",
    title = "amazing",
    description = "not kidding"
  )

  val user = UserRow(
    id = UserId.Test,
    email = "example@mail.com",
    firstName = "roman",
    lastName = "lebid",
    passwordMd = "pass",
    phone = "+380508453060",
    address = "just around the corner"
  )

  val product = ProductRow(
    id = ProductId.Test,
    name = "hoodie",
    title = "Star Wars Hoodie",
    slug = "sw_hoodie",
    description = "The most amazing hoodie in your life",
    categoryId = CategoryId(1),
    media = "https://images-na.ssl-images-amazon.com/images/I/81Qdx9MFLgL._UX385_.jpg",
    price = 100
  )

  val createCategories = Categories.schema.create
  val createProducts = Products.schema.create
  val createFilters = Filters.schema.create
  val createUsers = Users.schema.create

  val insertCategories = Categories += category
  val insertUsers = Users += user
  val insertProducts = Products += product

  val future = db run (
    createCategories andThen
      createProducts andThen
      createFilters andThen
      createUsers andThen
      insertCategories andThen
      insertUsers andThen
    insertProducts)

  val result = Await.result(future, 2.seconds)

  val productDb = new ProductServiceImpl.Db(db)
  val productService = new ProductServiceImpl(productDb)
  val productHttp = new ProductHttp(productService)

  val userDb = new UserServiceImpl.Db(db)
  val userService = new UserServiceImpl(userDb)
  val userHttp = new UserHttp(userService)

  val route = MainRoute.route ~ productHttp.route ~ userHttp.route

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8081)

  println(s"Server online at http://localhost:8081/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ ⇒ system.terminate())
}
