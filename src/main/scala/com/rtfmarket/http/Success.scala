package com.rtfmarket.http

import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCode, StatusCodes}
import play.api.libs.json._

trait Data[T] { self: T =>
  def toJson(implicit writes: Writes[T]): JsValue = Json.toJson(self)
}

case class Just(status: Int, data: Option[JsValue]) {
  def toResponse: HttpResponse = {
    HttpResponse(
      status = status,
      entity = HttpEntity(`application/json`, Json.prettyPrint(Json.toJson(this)))
    )
  }
}

object Just {
  def apply[T](data: Data[T], statusCode: StatusCode = StatusCodes.OK)(implicit writes: Writes[T]): Just = {
    Just(statusCode.intValue, Some(data.toJson))
  }

  def apply(statusCode: StatusCode): Just = Just(statusCode.intValue, None)

  implicit val ResponseFormat: OFormat[Just] = Json.format[Just]
}
