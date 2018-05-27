package com.rtfmarket.http

import akka.http.scaladsl.marshallers.playjson.PlayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.rtfmarket.services.OrderService
import com.rtfmarket.slick._
import IdMatchers._
import play.api.libs.json.{Json, OFormat}
import OrderHttp.OrderFormat

import scala.util.Success

class OrderHttp(orderService: OrderService) {
  val route: Route =
    pathPrefix("orders") {
      pathEndOrSingleSlash {
        (post & entity(as[OrderRow])) { order =>
          onComplete(orderService.placeOrder(UserId.Default, order).future) {
            case Success(Right(_))      =>
              complete(StatusCodes.OK)
            case Success(Left(message)) =>
              complete(Error(StatusCodes.BadRequest, message).toResponse)
            case _                      =>
              complete(StatusCodes.InternalServerError)
          }
        } ~
        get {
          onComplete(orderService.orders(UserId.Default).future) {
            case Success(Right(_)) =>
              complete(StatusCodes.OK)
            case Success(Left(message)) =>
              complete(Error(StatusCodes.BadRequest, message).toResponse)
            case _ =>
              complete(StatusCodes.InternalServerError)
          }
        }
      } ~
      path(Id[OrderId]) { id =>
        get {
          onComplete(orderService.order(id).future) {
            case Success(Right(_)) =>
              complete(StatusCodes.OK)
            case Success(Left(message)) =>
              complete(Error(StatusCodes.NotFound, message).toResponse)
            case _ =>
              complete(StatusCodes.InternalServerError)
          }
        }
      } ~
      pathPrefix("delivery") {
        pathEndOrSingleSlash {
          get {
            complete(StatusCodes.OK)
          }
        } ~
        path(Segment) { deliveryId =>
          get {
            complete(StatusCodes.OK)
          }
        }
      }
    }
}

object OrderHttp {
  import IdFormats._

  implicit val OrderFormat: OFormat[OrderRow] = Json.format[OrderRow]
}
