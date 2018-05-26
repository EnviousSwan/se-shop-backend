package com.rtfmarket.http

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.rtfmarket.services.PlayerAuthService

class UserRoute(playerAuthService: PlayerAuthService) {

  val route: Route =
    path("user") {
      pathEndOrSingleSlash {
        post {
          complete(StatusCodes.OK)
        }
      } ~
      path("login") {
        get {
          complete(StatusCodes.OK)
        }
      } ~
      path ("logout") {
        get {
          complete(StatusCodes.OK)
        }
      }
      path (Segment) { id =>
        get {
          complete(StatusCodes.OK)
        } ~
        post {
          complete(StatusCodes.OK)
        } ~
        delete {
          complete(StatusCodes.OK)
        }
      }
    }

}
