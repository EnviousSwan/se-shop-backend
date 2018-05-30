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
import com.softwaremill.session.SessionManager

import scala.concurrent.ExecutionContext

class OrderHttp(orderService: OrderService)
  (implicit val sessionManager: SessionManager[UserId],
    val executionContext: ExecutionContext) extends HttpRoute {

  val route: Route =
    pathPrefix("orders") {
      pathEndOrSingleSlash {
        (post & withSession) { userId => implicit context =>
          handleInContext(orderService.placeOrder(userId).future, StatusCodes.BadRequest)
        } ~
        (get & withSession) { userId => implicit context =>
          orderService.orders(userId).future flatMap {
            case Right(orders) =>
              context.complete(Just(orders).toResponse)
            case Left(message) =>
              context.complete(Error(StatusCodes.BadRequest, message).toResponse)
          } recoverWith {
            case e => context.complete(StatusCodes.InternalServerError)
          }
        }
      } ~
      path(Id[OrderId]) { id =>
        (get & withSession) { userId => implicit context =>
          orderService.order(id).future flatMap {
            case Right(order)  =>
              context.complete(Just(order).toResponse)
            case Left(message) =>
              context.complete(Error(StatusCodes.NotFound, message).toResponse)
          } recoverWith {
            case e => context.complete(StatusCodes.InternalServerError)
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
