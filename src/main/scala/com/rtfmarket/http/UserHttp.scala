package com.rtfmarket.http

import akka.http.scaladsl.marshallers.playjson.PlayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.rtfmarket.domain.User
import com.rtfmarket.http.IdMatchers._
import com.rtfmarket.http.UserHttp._
import com.rtfmarket.services.UserService
import com.rtfmarket.services.UserServiceImpl.UserFormat
import com.rtfmarket.slick.{UserId, UserRow}
import com.softwaremill.session.SessionDirectives._
import com.softwaremill.session.SessionOptions._
import com.softwaremill.session.{InMemoryRefreshTokenStorage, SessionConfig, SessionManager}
import com.typesafe.scalalogging.LazyLogging
import play.api.libs.json._

import scala.concurrent.ExecutionContext
import scala.util.Success

class UserHttp(userService: UserService)
  (implicit val sessionManager: SessionManager[String],
    val executionContext: ExecutionContext) extends HttpRoute with LazyLogging {

  val route: Route =
    pathPrefix("user") {
      pathEndOrSingleSlash {
        (post & entity(as[User])) { request =>
          handleUnit(userService.createUser(request).future, StatusCodes.BadRequest)
        }
      } ~
        path("login") {
          (post & entity(as[LoginRequest])) { req =>
            onComplete(userService.userExists(req.email)) {

              case Success(false) =>
                complete(Error(StatusCodes.NotFound, s"No user found with email ${ req.email }").toResponse)

              case Success(true)  =>
                onComplete(userService.loginUser(req.email, req.password).future) {
                  case Success(Right(_))      =>
                    mySetSession(req.email) {
                      complete(Just(StatusCodes.OK).toResponse)
                    }

                    case Success(Left(message)) =>
                      complete(Error(StatusCodes.BadRequest, message).toResponse)
                    case _                      =>
                      complete(StatusCodes.InternalServerError)
                  }
                case _              =>
                  complete(StatusCodes.InternalServerError)
              }
            }
        } ~
        path("logout") {
          get {
            requiredSession { session =>
              invalidateSession { context =>
                context.complete(StatusCodes.OK)
              }
            }
          }
        } ~
        path(Id[UserId]) { id =>
          get {
            requiredSession { userId => context =>
              logger.info("Current session: " + userId)
              handleIn(context)(userService.findUser(userId).future, StatusCodes.NotFound, withBody = true)
            }
          } ~
            (put & entity(as[User])) { req =>
              onComplete(userService.userExists(id)) {
                case Success(false) =>
                  complete(Error(StatusCodes.NotFound, s"No user found with id $id").toResponse)
                case Success(true) =>
                  handleUnit(userService.updateUser(req).future, StatusCodes.BadRequest)
                case _ =>
                  complete(StatusCodes.InternalServerError)
              }
            } ~
            delete {
              handleUnit(userService.deleteUser(id).future, StatusCodes.NotFound)
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