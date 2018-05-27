package com.rtfmarket.http

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.rtfmarket.http.CartHttp.Cart
import com.rtfmarket.http.ProductHttp.Product
import com.rtfmarket.services.{CartService, ProductService}
import com.rtfmarket.slick.{CartRow, UserId}
import play.api.libs.json.{Json, OFormat}

import scala.concurrent.ExecutionContext
import scala.util.Success

class CartHttp(cartService: CartService, productService: ProductService)
  (implicit executionContext: ExecutionContext) {

  val route: Route =
    pathPrefix("cart") {
      pathEndOrSingleSlash {
        get {
          onComplete(cartService.cart(UserId.Test).future) {
            case Success(Right(cart)) =>
              complete(Just(Cart(cart)).toResponse)
            case Success(Left(message)) =>
              complete(Error(StatusCodes.NotFound, message).toResponse)
            case _ =>
              complete(StatusCodes.InternalServerError)
          }
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

            onComplete(result.future) {
              case Success(Right(_))      =>
                complete(Just(StatusCodes.OK).toResponse)
              case Success(Left(message)) =>
                complete(Error(StatusCodes.NotFound, message).toResponse)
              case _                      =>
                complete(StatusCodes.InternalServerError)
            }
          }
        }
      }
    }

  private def handleProductRemoval(slug: String): Route = {
    val result = for {
      product <- productService.product(slug)
      _ <- cartService.removeProduct(UserId.Test, product.id)
    } yield ()

    onComplete(result.future) {
      case Success(Right(_))      =>
        complete(Just(StatusCodes.OK).toResponse)
      case Success(Left(message)) =>
        complete(Error(StatusCodes.NotFound, message).toResponse)
      case _                      =>
        complete(StatusCodes.InternalServerError)
    }
  }
}

object CartHttp {

  case class CartItem(count: Int = 1, products: List[Product] = List(Product()))

  case class Cart(cartItems: List[CartItem] = List(CartItem())) extends Data[Cart]

  object Cart {
    def apply(cartRow: CartRow): Cart = Cart()
  }

  implicit val CartItemFormat: OFormat[CartItem] = Json.format[CartItem]
  implicit val CartFormat: OFormat[Cart] = Json.format[Cart]
}
