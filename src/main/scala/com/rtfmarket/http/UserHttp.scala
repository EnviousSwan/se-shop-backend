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
import play.api.libs.json._

import scala.util.Success

class UserHttp(userService: UserService) extends HttpRoute {

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
                complete(Error(StatusCodes.NotFound, s"No user found with email ${req.email}").toResponse)
              case Success(true) =>
                handleUnit(userService.loginUser(req.email, req.password).future, StatusCodes.BadRequest)
              case _ =>
                complete(StatusCodes.InternalServerError)
            }
          }
        } ~
        path("logout") {
          get {
            handleUnit(userService.logoutUser(UserId(1)).future, StatusCodes.NotFound)
          }
        } ~
        path(Id[UserId]) { id =>
          get {
            handle(userService.findUser(id).future, StatusCodes.NotFound, withBody = true)
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