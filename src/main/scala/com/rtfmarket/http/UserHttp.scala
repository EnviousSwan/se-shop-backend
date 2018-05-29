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

class UserHttp(userService: UserService) {

  val route: Route =
    pathPrefix("user") {
      pathEndOrSingleSlash {
        (post & entity(as[User])) { request =>
          onComplete(userService.createUser(request).future) {
            case Success(Right(_))      =>
              complete(Just(StatusCodes.OK).toResponse)
            case Success(Left(message)) =>
              complete(Error(StatusCodes.BadRequest, message).toResponse)
            case _                      =>
              complete(StatusCodes.InternalServerError)
          }
        }
      } ~
      path("login") {
        (post & entity(as[LoginRequest])) { req =>
          onComplete(userService.userExists(req.email)) {
            case Success(false) =>
              complete(Error(StatusCodes.NotFound, s"No user found with email ${req.email}").toResponse)
            case Success(true) =>
              onComplete(userService.loginUser(req.email, req.password).future) {
                case Success(Right(user))      =>
                  complete(Just(StatusCodes.OK).toResponse)
                case Success(Left(message)) =>
                  complete(Error(StatusCodes.BadRequest, message).toResponse)
                case _                      =>
                  complete(StatusCodes.InternalServerError)
              }
            case _ =>
              complete(StatusCodes.InternalServerError)
          }
        }
      } ~
      path("logout") {
        get {
          onComplete(userService.logoutUser(UserId(1)).future) {
            case Success(Right(_))      =>
              complete(Just(StatusCodes.OK).toResponse)
            case Success(Left(message)) =>
              complete(Error(StatusCodes.NotFound, message).toResponse)
            case _                      =>
              complete(StatusCodes.InternalServerError)
          }
        }
      } ~
      path(Id[UserId]) { id =>
        get {
          onComplete(userService.findUser(id).future) {
            case Success(Right(user))   =>
              complete(Just(user).toResponse)
            case Success(Left(message)) =>
              complete(Error(StatusCodes.NotFound, message).toResponse)
            case _                      =>
              complete(StatusCodes.InternalServerError)
          }
        } ~
        (put & entity(as[User])) { req =>
          onComplete(userService.userExists(id)) {
            case Success(false) =>
              complete(Error(StatusCodes.NotFound, s"No user found with id $id").toResponse)
            case Success(true) =>
              onComplete(userService.updateUser(req).future) {
                case Success(Right(_)) =>
                  complete(Just(StatusCodes.OK).toResponse)
                case Success(Left(message)) =>
                  complete(Error(StatusCodes.BadRequest, message).toResponse)
                case _ =>
                  complete(StatusCodes.InternalServerError)
              }
            case _ =>
              complete(StatusCodes.InternalServerError)
          }
        } ~
        delete {
          onComplete(userService.deleteUser(id).future) {
            case Success(Right(_))      =>
              complete(Just(StatusCodes.OK).toResponse)
            case Success(Left(message)) =>
              complete(Error(StatusCodes.NotFound, message).toResponse)
            case _                      =>
              complete(StatusCodes.InternalServerError)
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