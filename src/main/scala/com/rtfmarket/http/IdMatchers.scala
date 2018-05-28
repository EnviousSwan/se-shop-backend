package com.rtfmarket.http

import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.server.PathMatcher.{Matched, Matching, Unmatched}
import akka.http.scaladsl.server.PathMatcher1
import com.rtfmarket.slick._
import play.api.libs.json._

import scala.reflect.ClassTag
import scala.util.control.NonFatal

object IdMatchers {
  type LongToId[ID] = Long => ID

  implicit val UserIdCtor: LongToId[UserId] = UserId.apply
  implicit val ProducIdCtor: LongToId[ProductId] = ProductId.apply
  implicit val CategoryIdCtor: LongToId[CategoryId] = CategoryId.apply
  implicit val OrderIdCtor: LongToId[OrderId] = OrderId.apply

  case class Id[ID : ClassTag]()(implicit ctor: Long => ID) extends PathMatcher1[ID] {
    def apply(path: Path): Matching[Tuple1[ID]] = path match {
      case Path.Segment(segment, tail) =>
        try Matched(tail, Tuple1(ctor(segment.toLong))) catch {
          case NonFatal(_) => Unmatched
        }
      case _ =>
        Unmatched
    }
  }
}

object IdFormats {
  private def jsonFormat[T](apply: Long => T, value: T => Long): Format[T] = new Format[T] {
    def writes(x: T): JsValue = JsNumber(value(x))
    def reads(json: JsValue): JsResult[T] = for (str <- json.validate[Long]) yield apply(str)
  }

  implicit val UserIdFormat: Format[UserId] = jsonFormat(UserId.apply, _.value)
  implicit val OrderIdFormat: Format[OrderId] = jsonFormat(OrderId.apply, _.value)
  implicit val ProductIdFormat: Format[ProductId] = jsonFormat(ProductId.apply, _.value)
  implicit val CartIdFormat: Format[CartId] = jsonFormat(CartId.apply, _.value)
  implicit val FilterIdFormat: Format[FilterId] = jsonFormat(FilterId.apply, _.value)
  implicit val CategoryIdFormat: Format[CategoryId] = jsonFormat(CategoryId.apply, _.value)
}
