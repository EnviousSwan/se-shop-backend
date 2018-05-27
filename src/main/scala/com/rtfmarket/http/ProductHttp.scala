package com.rtfmarket.http

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.rtfmarket.http.IdMatchers._
import com.rtfmarket.services.ProductService
import com.rtfmarket.slick.{CategoryId, CategoryRow, ProductId}
import play.api.libs.json.{Json, OFormat}
import ProductHttp._

import scala.concurrent.ExecutionContext
import scala.util.Success

class ProductHttp(productService: ProductService)(implicit executionContext: ExecutionContext) {

  val route: Route =
    pathPrefix("products") {
      path("category" / Segment) { slug =>
        parameters('sort.?, 'order.?, "filter[]".?) { (sort, order, filters) =>
          get {
            onComplete(productService.category(slug).future) {
              case Success(Right(category))      =>
                complete(Response(Json toJson Category(category)).toResponse)
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

object ProductHttp {

  case class Media(
    main: String = "http://via.placeholder.com/300",
    fallback: String = "http://via.placeholder.com/300")

  case class Property(id: Long = 0, name: String = "name", title: String = "title")

  case class Filter(
    id: Long = 0,
    name: String = "name",
    title: String = "title",
    description: String = "description",
    multiple: Boolean = false,
    options: List[FilterOption] = List(FilterOption())
  )

  case class FilterOption(id: Long = 0, name: String = "name", title: String = "title")

  case class Product(
    id: ProductId = ProductId.Default,
    name: String = "name",
    title: String = "title",
    description: String = "description",
    categoryId: CategoryId = CategoryId.Default,
    media: Media = Media(),
    price: Double = 100,
    properties: List[Property] = List(Property()),
    inCart: Boolean = false,
    inFavourite: Boolean = false,
    createDate: String = "1970-01-01"
  )

  case class Category(
    id: CategoryId = CategoryId.Default,
    name: String = "name",
    slug: String = "slug",
    title: String = "title",
    description: String = "description",
    products: List[Product] = List(Product()),
    filters: List[Filter] = List(Filter())
  )

  object Category {
    def apply(row: CategoryRow): Category =
      Category(row.id, row.name, row.slug, row.title)
  }

  import IdFormats._

  implicit val MediaWrites: OFormat[Media] = Json.format[Media]
  implicit val PropertyWrites: OFormat[Property] = Json.format[Property]
  implicit val FilterOptionWrites: OFormat[FilterOption] = Json.format[FilterOption]
  implicit val FilterWrite: OFormat[Filter] = Json.format[Filter]
  implicit val ProductWrites: OFormat[Product] = Json.format[Product]
  implicit val CategoryWrites: OFormat[Category] = Json.format[Category]
}
