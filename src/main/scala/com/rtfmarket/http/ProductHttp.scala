package com.rtfmarket.http

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.rtfmarket.http.IdMatchers._
import com.rtfmarket.services.ProductService
import com.rtfmarket.slick.CategoryId

import scala.concurrent.ExecutionContext
import scala.util.Success

class ProductHttp(productService: ProductService)(implicit executionContext: ExecutionContext) {

  val route: Route =
    pathPrefix("products") {
      path("category" / Id[CategoryId]) { id =>
        parameters('sort.?, 'order.?, "filter[]".?) { (sort, order, filters) =>
          get {
            onComplete(productService.category(id).future) {
              case Success(Right(_))      =>
                complete(StatusCodes.OK)
              case Success(Left(message)) =>
                complete(Error(StatusCodes.NotFound, message).toResponse)
              case _                      =>
                complete(StatusCodes.InternalServerError)
            }
          }
        }
      } ~
      path("details" / Segment) { slug =>
        get {
          onComplete(productService.productDetails(slug).future) {
            case Success(Right(_))      =>
              complete(StatusCodes.OK)
            case Success(Left(message)) =>
              complete(Error(StatusCodes.NotFound, message).toResponse)
            case _                      =>
              complete(StatusCodes.InternalServerError)
          }
        }
      } ~
      path(Segment) { slug =>
        get {
          onComplete(productService.product(slug).future) {
            case Success(Right(_)) =>
              complete(StatusCodes.OK)
            case Success(Left(message)) =>
              complete(Error(StatusCodes.NotFound, message).toResponse)
            case _ =>
              complete(StatusCodes.InternalServerError)
          }
        }
      }
    }
}

object ProductHttp
