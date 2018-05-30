package com.rtfmarket.http

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives.{complete, onComplete}
import akka.http.scaladsl.server._
import com.evolutiongaming.util.Validation.V
import com.softwaremill.session.SessionDirectives.{setSession, invalidateSession => invalidateS, requiredSession => requireS}
import com.softwaremill.session.SessionManager
import com.softwaremill.session.SessionOptions.{oneOff, usingHeaders}
import play.api.libs.json.Writes

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

trait HttpRoute {

  implicit val executionContext: ExecutionContext
  implicit val sessionManager: SessionManager[String]

  def mySetSession(v: String): Directive0 = setSession(oneOff, usingHeaders, v)

  protected val requiredSession: Directive1[String] = requireS(oneOff, usingHeaders)
  protected val invalidateSession: Directive0 = invalidateS(oneOff, usingHeaders)

  def handleIn[T <: Data[T]](context: RequestContext)
    (future: => Future[V[T]], errorCode: StatusCode, withBody: Boolean = false)
    (implicit writes: Writes[T]): Future[RouteResult] =

    future flatMap {
      case Right(entity)      =>
        if (withBody)
          context.complete(Just(entity).toResponse)
        else
          context.complete(Just(StatusCodes.OK).toResponse)
      case Left(message) =>
        context.complete(Error(errorCode, message).toResponse)
      case _                      =>
        context.complete(StatusCodes.InternalServerError)
    }

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
