package com.rtfmarket.http

import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCode, StatusCodes}
import Response.ResponseFormat
import play.api.libs.json._

trait Data

case class Response(status: Int, data: JsValue) {
  def toResponse: HttpResponse = HttpResponse(
    status = status,
    entity = HttpEntity(`application/json`, Json.prettyPrint(Json.toJson(this)))
  )
}

object Response {
  def apply(data: JsValue, statusCode: StatusCode = StatusCodes.OK): Response =
    Response(statusCode.intValue, data)

  implicit val ResponseFormat: OFormat[Response] = Json.format[Response]
}
