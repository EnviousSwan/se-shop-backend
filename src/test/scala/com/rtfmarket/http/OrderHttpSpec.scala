package com.rtfmarket.http

import akka.http.scaladsl.marshallers.playjson.PlayJsonSupport._
import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.rtfmarket.services.OrderService
import org.mockito.Mockito._
import com.rtfmarket.slick.{CartId, OrderId, OrderRow, UserId}
import org.mockito.{ArgumentMatchers => M}
import org.scalatest.{Matchers, WordSpec}
import org.scalatest.mockito.MockitoSugar
import com.evolutiongaming.util.Validation._
import OrderHttp._

class OrderHttpSpec extends HttpSpec {
  "/orders" should {
    "respond with BadRequest on invalid order" in new Scope {
      when(orderService.placeOrder(UserId(M.anyLong()), M.any())) thenReturn "Invalid order".ko.fe

      Post("/orders", badOrder) ~> service.route ~> check {
        checkForError(StatusCodes.BadRequest, "Invalid order")
      }
    }

    "place new order on POST" in new Scope {
      when(orderService.placeOrder(UserId.Test, order)) thenReturn ().ok.fe[String]

      Post("/orders", order) ~> service.route ~> check {
        status shouldBe StatusCodes.OK
      }
    }

    "respond with BadRequest on invalid request" in new Scope {
      when(orderService.orders(UserId(M.anyLong()))) thenReturn "Invalid request".ko.fe

      Get("/orders") ~> service.route ~> check {
        checkForError(StatusCodes.BadRequest, "Invalid request")
      }
    }

    "get orders list" in new Scope {
      when(orderService.orders(UserId.Test)) thenReturn List.empty.ok.fe[String]

      Get("/orders") ~> service.route ~> check {
        status shouldBe StatusCodes.OK
      }
    }
  }

  "/orders/id" should {
    "return NotFound if no order with such id exists" in new Scope {
      when(orderService.order(OrderId(M.anyLong))) thenReturn "No order with such id exists".ko.fe

      Get(s"/orders/1") ~> service.route ~> check {
        checkForError(StatusCodes.NotFound, "No order with such id exists")
      }
    }

    "return order details for the user" in new Scope {
      when(orderService.order(orderId)) thenReturn order.ok.fe[String]

      Get(s"/orders/${orderId.value}") ~> service.route ~> check {
        status shouldBe StatusCodes.OK
      }
    }
  }

  "/orders/delivery" should {
    "return list of delivery methods" in new Scope {
      Get("/orders/delivery") ~> service.route ~> check {
        status shouldBe StatusCodes.OK
      }
    }
  }

  "/orders/delivery/id" should {
    "return delivery method details" in new Scope {
      Get(s"/orders/delivery/$deliveryId") ~> service.route ~> check {
        status shouldBe StatusCodes.OK
      }
    }
  }


  trait Scope {
    val orderId = OrderId(42)
    val deliveryId = 100

    val order = OrderRow(OrderId.Default, UserId.Test, CartId.Test)
    val badOrder = OrderRow(OrderId(1), UserId(1), CartId(1))

    val orderService: OrderService = mock[OrderService]
    val service = new OrderHttp(orderService)
  }
}
