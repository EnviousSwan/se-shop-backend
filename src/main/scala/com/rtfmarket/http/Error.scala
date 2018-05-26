package com.rtfmarket.http

import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCode}
import play.api.libs.json._

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
