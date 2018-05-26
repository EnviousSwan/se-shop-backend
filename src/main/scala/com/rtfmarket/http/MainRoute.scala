package com.rtfmarket.http

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, get, path}
import akka.http.scaladsl.server.Route

object MainRoute {
  val route: Route =
    path("check") {
      get {
        complete(StatusCodes.OK)
      }
    }
}
