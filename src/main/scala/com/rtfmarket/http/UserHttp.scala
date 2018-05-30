package com.rtfmarket.http

import akka.http.scaladsl.marshallers.playjson.PlayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive0, Route}
import com.rtfmarket.domain.User
import com.rtfmarket.http.IdMatchers._
import com.rtfmarket.http.UserHttp._
import com.rtfmarket.services.UserService
import com.rtfmarket.services.UserServiceImpl.UserFormat
import com.rtfmarket.slick.{UserId, UserRow}
import com.softwaremill.session.SessionManager
import com.softwaremill.session.SessionOptions.{oneOff, usingHeaders}
import com.softwaremill.session.SessionDirectives.{setSession => setS, invalidateSession => invalidateS}
import com.typesafe.scalalogging.LazyLogging
import play.api.libs.json._

import scala.concurrent.ExecutionContext
import scala.util.Success

class UserHttp(userService: UserService)
  (implicit val sessionManager: SessionManager[UserId],
    val executionContext: ExecutionContext) extends HttpRoute with LazyLogging {

  private val invalidateSession: Directive0 = invalidateS(oneOff, usingHeaders)

  private def setSession(v: UserId): Directive0 = setS(oneOff, usingHeaders, v)

  val route: Route =
    pathPrefix("user") {
      pathEndOrSingleSlash {
        (post & entity(as[User])) { request =>
          handle(userService.createUser(request).future, StatusCodes.BadRequest)
        } ~
        (get & withSession) { id =>
          implicit context =>
            handleInContext(userService.findUser(id).future, StatusCodes.NotFound)
        } ~
        (put & entity(as[User])) { req =>
          onComplete(userService.userExists(req.email)) {
            case Success(false) => complete(Error(StatusCodes.NotFound, "No user found with such email").toResponse)
            case Success(true)  =>
              withSession { _ =>
                implicit context =>
                  handleInContext(userService.updateUser(req).future, StatusCodes.BadRequest)
              }
            case _              => complete(StatusCodes.InternalServerError)
          }
        } ~
        (delete & withSession) { userId =>
          implicit context =>
            handleInContext(userService.deleteUser(userId).future, StatusCodes.NotFound)
        }
      } ~
      (path("login") & post & entity(as[LoginRequest])) { req =>
        onComplete(userService.userExists(req.email)) {
          case Success(false) =>
            complete(Error(StatusCodes.NotFound, s"No user found with email ${ req.email }").toResponse)

          case Success(true) =>
            onComplete(userService.loginUser(req.email, req.password).future) {
              case Success(Right(userId)) =>
                setSession(userId) {
                  complete(Just(StatusCodes.OK).toResponse)
                 }
              case Success(Left(message)) =>
                complete(Error(StatusCodes.BadRequest, message).toResponse)
              case _                      =>
                complete(StatusCodes.InternalServerError)
            }
          case _             =>
            complete(StatusCodes.InternalServerError)
          }
      } ~
      path("logout") {
        get {
          withSession { id =>
            invalidateSession { context =>
              context.complete(StatusCodes.OK)
            }
          }
        }
      }
    }
}

object UserHttp {

  import IdFormats.UserIdFormat

  implicit val userFormat: OFormat[UserRow] = Json.format[UserRow]

  case class LoginRequest(email: String, password: String)

  implicit val loginRequestFormat: OFormat[LoginRequest] = Json.format[LoginRequest]
}
