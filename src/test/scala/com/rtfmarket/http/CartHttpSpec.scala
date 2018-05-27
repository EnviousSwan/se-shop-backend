package com.rtfmarket.http

import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.marshallers.playjson.PlayJsonSupport._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.rtfmarket.services.{CartService, ProductService}
import com.rtfmarket.slick.{CategoryId, ProductId, ProductRow, UserId}
import org.scalatest.{Matchers, WordSpec}
import org.mockito.Mockito._
import org.mockito.{ArgumentMatchers => M}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.mockito.MockitoSugar
import com.evolutiongaming.util.Validation._
import Error._

class CartHttpSpec extends WordSpec with Matchers with ScalatestRouteTest with MockitoSugar {
  "/cart" should {
    "return products in user's cart" in new Scope {
      Get("/cart") ~> service.route ~> check {
        status shouldBe StatusCodes.OK
      }
    }
  }

  "/cart/product/slug" should {
    "respond with NotFound if no such product exists" in new Scope {
      when(productService.product("other")) thenReturn s"No product found for 'other'".ko.fe

      Post(s"/cart/product/other") ~> service.route ~> check {
        status shouldBe StatusCodes.NotFound
        contentType shouldBe ContentTypes.`application/json`
        responseAs[Error] shouldBe Error(404, s"No product found for 'other'")
      }
    }

    "add new product to cart" in new Scope {
      when(productService.product(slug)) thenReturn sweater.ok.fe[String]
      when(cartService.addProduct(UserId.Default, sweater)) thenReturn ().ok.fe[String]

      Post(s"/cart/product/$slug") ~> service.route ~> check {
        status shouldBe StatusCodes.OK
      }
    }

    "respond with NotFound if there's no such product" in new Scope {
      when(productService.product("other")) thenReturn "No product 'other' in user's cart".ko.fe

      Delete(s"/cart/product/other") ~> service.route ~> check {
        status shouldBe StatusCodes.NotFound
        contentType shouldBe ContentTypes.`application/json`
        responseAs[Error] shouldBe Error(404, "No product 'other' in user's cart")
      }
    }

    "respond with NotFound if no such product present in user's cart" ignore new Scope { }

    "remove product from cart" in new Scope {
      when(productService.product(slug)) thenReturn sweater.ok.fe[String]
      when(cartService.removeProduct(UserId.Default, sweater.id)) thenReturn ().ok.fe[String]

      Delete(s"/cart/product/$slug") ~> service.route ~> check {
        status shouldBe StatusCodes.OK
      }
    }

    "respond with NotFound if no product with such slug exists" in new Scope {
      when(productService.product("other")) thenReturn s"No product found for 'other'".ko.fe

      Put(s"/cart/product/other") ~> service.route ~> check {
        status shouldBe StatusCodes.NotFound
        contentType shouldBe ContentTypes.`application/json`
        responseAs[Error] shouldBe Error(404, "No product found for 'other'")
      }
    }

    "change quantity of product in user's cart" in new Scope {
      when(productService.product(slug)) thenReturn sweater.ok.fe[String]
      when(cartService.changeProductQuantity(UserId.Default, sweater.id, 2)) thenReturn ().ok.fe[String]

      Put(s"/cart/product/$slug") ~> service.route ~> check {
        status shouldBe StatusCodes.OK
      }
    }
  }

  trait Scope {
    val slug = "slug"

    val sweater = ProductRow(
      id = ProductId.Default,
      name = "Gucci",
      title = "sweater",
      description = "nice and warm",
      categoryId = CategoryId(0),
      media = "just.url",
      price = 100500
    )

    val cartService = mock[CartService]
    val productService = mock[ProductService]

    val service = new CartHttp(cartService, productService)
  }
}

