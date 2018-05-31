import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.stream.ActorMaterializer
import akka.http.scaladsl.model.headers.HttpOriginRange
import com.rtfmarket.http.{MainRoute, ProductHttp, UserHttp}
import com.rtfmarket.services.{ProductServiceImpl, UserServiceImpl}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, RejectionHandler}
import ch.megard.akka.http.cors.scaladsl.CorsDirectives
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
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

  val user = UserRow(
    id = UserId.Test,
    email = "example@mail.com",
    firstName = "roman",
    lastName = "lebid",
    passwordMd = "pass",
    phone = "+380508453060",
    address = "just around the corner"
  )

  val cat1 = CategoryRow(CategoryId.Test, "clothing", "clothing", "clothing", "Different wearable items of clothing")
  val cat2 = CategoryRow(CategoryId.Test, "jewelry", "jewelry", "jewelry", "Rings, bracelets and watches")

  val cats = List(cat1, cat2)

  val p1 = ProductRow(
    id = ProductId.Test,
    name = "hoodie",
    title = "Star Wars Hoodie",
    slug = "sw_hoodie",
    description = "The most amazing hoodie in your life",
    categoryId = CategoryId(1),
    media = "https://images-na.ssl-images-amazon.com/images/I/81Qdx9MFLgL._UX385_.jpg",
    price = 100
  )

  val p2 = ProductRow(
    id = ProductId.Test,
    name = "t-shirt",
    title = "Jusrassic Park T-Shirt",
    slug = "jp_t_shirt",
    description = "Hawaiian T-shirt right from the park",
    categoryId = CategoryId(1),
    media = "http://i.ebayimg.com/00/s/NTAwWDQ4MA==/z/TJUAAMXQ74JTVk7k/$_3.JPG?set_id=2",
    price = 10
  )

  val p3 = ProductRow(
    id = ProductId.Test,
    name = "ring",
    title = "LOTR ring",
    slug = "lotr_ring",
    description = "One ring to rule them all",
    categoryId = CategoryId(2),
    media = "https://cdn3.volusion.com/sc7ta.cvhr3/v/vspfiles/photos/GLD-LOTR-2.jpg?1513241922",
    price = 666
  )

  val p4 = ProductRow(
    id = ProductId.Test,
    name = "watch",
    title = "Harry Potter Time Turner",
    slug = "hp_watch",
    description = "Turn the time if you need some",
    categoryId = CategoryId(2),
    media = "https://vignette.wikia.nocookie.net/harrypotter/images/a/a4/Time_Turner.png/revision/latest?cb=20161126042527",
    price = 123
  )

  val products = List(p1, p2, p3, p4)

  val createCategories = Categories.schema.create
  val createProducts = Products.schema.create
  val createFilters = Filters.schema.create
  val createUsers = Users.schema.create

  val insertUsers = Users += user
  val insertCategories = Categories ++= cats
  val insertProducts = Products ++= products

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


  val settings = CorsSettings.defaultSettings
    .withAllowGenericHttpRequests(true)
    .withAllowCredentials(true)
    .withAllowedOrigins(HttpOriginRange.*)

  val rejectionHandler = corsRejectionHandler withFallback RejectionHandler.default

  val exceptionHandler = ExceptionHandler {
    case e: NoSuchElementException => complete(StatusCodes.NotFound -> e.getMessage)
  }

  val handleErrors = handleRejections(rejectionHandler) & handleExceptions(exceptionHandler)

  val route =
    handleRejections(CorsDirectives.corsRejectionHandler) {
      cors(settings) {
        MainRoute.route ~ productHttp.route ~ userHttp.route
    }
  }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8081)

  println(s"Server online at http://localhost:8081/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ ⇒ system.terminate())
}
