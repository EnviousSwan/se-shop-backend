package com.rtfmarket.http

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.rtfmarket.services.ProductService
import com.rtfmarket.slick._
import com.rtfmarket.domain._
import com.rtfmarket.services.ProductServiceImpl._

import scala.concurrent.ExecutionContext
import scala.util.Success

class ProductHttp(productService: ProductService
)(implicit executionContext: ExecutionContext) extends HttpRoute {

  val route: Route =
    path("categories") {
      get {
        onComplete(productService.categories()) {
          case Success(categories) =>
            complete(Just(categories).toResponse)
          case _                          =>
            complete(StatusCodes.InternalServerError)
        }
      }
    } ~
    pathPrefix("products") {
      path("category" / Segment) { slug =>
        parameters('sort.?, 'order.?, "filter[]".?) { (sort, order, filters) =>
          get {
            handle(productService.category(slug).future, StatusCodes.NotFound)
          }
        }
      } ~
      path(Segment / "details") { slug =>
        get {
          onComplete(productService.productDetails(slug).future) {
            case Success(Right(details)) =>
              complete(Just(ProductDetails(details)).toResponse)
            case Success(Left(message))  =>
              complete(Error(StatusCodes.NotFound, message).toResponse)
            case _                       =>
              complete(StatusCodes.InternalServerError)
          }
        }
      } ~
      path(Segment) { slug =>
        get {
          onComplete(productService.product(slug).future) {
            case Success(Right(product)) =>
              complete(Just(Product(product)).toResponse)
            case Success(Left(message)) =>
              complete(Error(StatusCodes.NotFound, message).toResponse)
            case _ =>
              complete(StatusCodes.InternalServerError)
          }
        }
      }
    }
}
