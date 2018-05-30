package com.rtfmarket.http

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives.{complete, onComplete}
import akka.http.scaladsl.server.Route
import com.evolutiongaming.util.Validation.V
import play.api.libs.json.Writes

import scala.concurrent.Future
import scala.util.Success

trait HttpRoute {
  def handle[T <: Data[T]](future: => Future[V[T]], errorCode: StatusCode, withBody: Boolean = false)
    (implicit writes: Writes[T]): Route =

    onComplete(future) {
      case Success(Right(entity))      =>
        if (withBody)
          complete(Just(entity).toResponse)
        else
          complete(Just(StatusCodes.OK).toResponse)
      case Success(Left(message)) =>
        complete(Error(errorCode, message).toResponse)
      case _                      =>
        complete(StatusCodes.InternalServerError)
    }

  def handleUnit(future: => Future[V[Unit]], errorCode: StatusCode): Route =
    onComplete(future) {
      case Success(Right(_))      =>
        complete(Just(StatusCodes.OK).toResponse)
      case Success(Left(message)) =>
        complete(Error(errorCode, message).toResponse)
      case _                      =>
        complete(StatusCodes.InternalServerError)
    }
}
