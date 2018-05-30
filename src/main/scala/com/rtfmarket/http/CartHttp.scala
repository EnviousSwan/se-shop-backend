package com.rtfmarket.http

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{RequestContext, Route, RouteResult}
import com.rtfmarket.services.{CartService, ProductService}
import com.rtfmarket.slick.UserId
import com.rtfmarket.services.CartServiceImpl.CartFormat
import com.softwaremill.session.SessionManager

import scala.concurrent.{ExecutionContext, Future}

class CartHttp(cartService: CartService, productService: ProductService)
  (implicit val sessionManager: SessionManager[UserId],
    val executionContext: ExecutionContext) extends HttpRoute {

  val route: Route =
    pathPrefix("cart") {
      pathEndOrSingleSlash {
        (get & withSession) { id => implicit context =>
          handleInContext(cartService.cart(id).future, StatusCodes.NotFound)
        }
      } ~
      path("product" / Segment) { slug =>
        (post & withSession) { id => implicit context =>
          val result = for {
            product <- productService.product(slug)
            _ <- cartService.addProduct(UserId.Test, product)
          } yield ()

          handleInContext(result.future, StatusCodes.NotFound)
        } ~
        (delete & withSession) { id => implicit context =>
          handleProductRemoval(slug)
        } ~
        (put & parameters('count.as[Int])) { count =>
          withSession { id => implicit context =>
            if (count == 0)
              handleProductRemoval(slug)
            else {
              val result = for {
                product <- productService.product(slug)
                _ <- cartService.changeProductQuantity(UserId.Test, product.id, 2)
              } yield ()

              handleInContext(result.future, StatusCodes.NotFound)
            }
          }
        }
      }
    }

  private def handleProductRemoval(slug: String)
    (implicit context: RequestContext): Future[RouteResult] = {
    val result = for {
      product <- productService.product(slug)
      _ <- cartService.removeProduct(UserId.Test, product.id)
    } yield ()

    handleInContext(result.future, StatusCodes.NotFound)
  }
}

