package com.rtfmarket.http

import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCode, StatusCodes}
import play.api.libs.json._

trait Data[T] { self: T =>
  def toJson(implicit writes: Writes[T]): JsValue = Json.toJson(self)
}

case class Response(status: Int, data: JsValue) {
  def toResponse: HttpResponse = HttpResponse(
    status = status,
    entity = HttpEntity(`application/json`, Json.prettyPrint(Json.toJson(this)))
  )
}

object Response {
  def apply[T](data: Data[T], statusCode: StatusCode = StatusCodes.OK)(implicit writes: Writes[T]): Response =
    Response(statusCode.intValue, data.toJson)

  implicit val ResponseFormat: OFormat[Response] = Json.format[Response]
}
