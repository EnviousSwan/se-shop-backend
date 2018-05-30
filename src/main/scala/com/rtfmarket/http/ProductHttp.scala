package com.rtfmarket.http

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.rtfmarket.services.ProductService
import com.rtfmarket.slick._
import com.rtfmarket.domain._
import com.rtfmarket.http.IdMatchers.Id
import com.rtfmarket.services.ProductServiceImpl._
import com.softwaremill.session.SessionManager
import IdMatchers._

import scala.concurrent.ExecutionContext
import scala.util.Success

class ProductHttp(productService: ProductService)
  (implicit val sessionManager: SessionManager[UserId],
    val executionContext: ExecutionContext) extends HttpRoute {

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
        parameters('sort.?, 'order.?) { (sort, order) =>
          get {
            handle(productService.category(slug).future, StatusCodes.NotFound)
          }
        }
      } ~
      path(Id[ProductId] / "details") { id =>
        get {
          handle(productService.productDetails(id).future, StatusCodes.NotFound)
        }
      } ~
      path(Segment) { slug =>
        get {
          handle(productService.product(slug).future, StatusCodes.NotFound)
        }
      }
    }

  private def sortedProducts(products: List[Product], sort: Option[String], order: Option[String]) =
    products sortWith { (a, b) =>
      sort match {
        case Some("price") =>
          order match {
            case Some("desc") => a.price > b.price
            case _            => a.price < b.price
          }
        case Some("date")  =>
          order match {
            case Some("desc") => a.createDate > b.createDate
            case _            => a.createDate < b.createDate
          }
        case _             => a.id.value < b.id.value
      }
    }
}
