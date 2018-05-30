package com.rtfmarket.http

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.rtfmarket.services.{CartService, ProductService}
import com.rtfmarket.slick.UserId
import com.rtfmarket.services.CartServiceImpl.CartFormat
import com.softwaremill.session.SessionManager

import scala.concurrent.ExecutionContext
import scala.util.Success

class CartHttp(cartService: CartService, productService: ProductService)
  (implicit val sessionManager: SessionManager[UserId],
    val executionContext: ExecutionContext) extends HttpRoute {

  val route: Route =
    pathPrefix("cart") {
      pathEndOrSingleSlash {
        get {
          handle(cartService.cart(UserId.Test).future, StatusCodes.NotFound)
        }
      } ~
      path("product" / Segment) { slug =>
        post {
          val result = for {
            product <- productService.product(slug)
            _ <- cartService.addProduct(UserId.Test, product)
          } yield ()

          onComplete(result.future) {
            case Success(Right(_)) =>
              complete(Just(StatusCodes.OK).toResponse)
            case Success(Left(message)) =>
              complete(Error(StatusCodes.NotFound, message).toResponse)
            case _ =>
              complete(StatusCodes.InternalServerError)
          }
        } ~
        delete {
          handleProductRemoval(slug)
        } ~
        (put & parameters('count.as[Int])) { count =>
          if (count == 0)
            handleProductRemoval(slug)
          else {
            val result = for {
              product <- productService.product(slug)
              _ <- cartService.changeProductQuantity(UserId.Test, product.id, 2)
            } yield ()

            handle(result.future, StatusCodes.NotFound)
          }
        }
      }
    }

  private def handleProductRemoval(slug: String): Route = {
    val result = for {
      product <- productService.product(slug)
      _ <- cartService.removeProduct(UserId.Test, product.id)
    } yield ()

    handle(result.future, StatusCodes.NotFound)
  }
}

