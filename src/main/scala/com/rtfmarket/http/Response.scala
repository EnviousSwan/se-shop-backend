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
  def apply[T](data: Seq[Data[T]])(implicit writes: Writes[T]): Just = {
    Just(StatusCodes.OK.intValue, Some(Json toJson data.map(_.toJson)))
  }

  def apply[T](data: Data[T], statusCode: StatusCode = StatusCodes.OK)(implicit writes: Writes[T]): Just = {
    Just(statusCode.intValue, Some(data.toJson))
  }

  def apply(statusCode: StatusCode): Just = Just(statusCode.intValue, None)

  implicit val ResponseFormat: OFormat[Just] = Json.format[Just]
}

case class Error(status: Int, message: String) {
  def toResponse: HttpResponse = HttpResponse(
    status = status,
    entity = HttpEntity(`application/json`, Json.prettyPrint(Json.toJson(this)))
  )
}

object Error {
  def apply(statusCode: StatusCode, message: String): Error = Error(statusCode.intValue, message)

  implicit val ErrorResponseFormat: OFormat[Error] = Json.format[Error]
}
