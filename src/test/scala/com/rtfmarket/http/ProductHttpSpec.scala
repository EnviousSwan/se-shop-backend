package com.rtfmarket.http

import akka.http.scaladsl.marshallers.playjson.PlayJsonSupport._
import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.evolutiongaming.util.Validation._
import com.rtfmarket.http.Error.ErrorResponseFormat
import com.rtfmarket.services.ProductService
import com.rtfmarket.slick._
import org.mockito.Mockito._
import org.mockito.{ArgumentMatchers => M}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Matchers, WordSpec}

class ProductHttpSpec extends WordSpec with Matchers with ScalatestRouteTest with MockitoSugar {

  "/products/category/id" should {
    "respond with NotFound no when such category exists" in new Scope {
      when(productService.category(CategoryId(M.any()))) thenReturn s"Category $id not found".ko.fe

      Get(s"/products/category/$id") ~> service.route ~> check {
        status shouldBe StatusCodes.NotFound
        contentType shouldBe ContentTypes.`application/json`
        responseAs[Error] shouldBe Error(404, s"Category $id not found")
      }
    }

    "respond with OK when category with such id exists" in new Scope {
      when(productService.category(clothes.id)) thenReturn clothes.ok.fe[String]

      Get(s"/products/category/${clothes.id.value}") ~> service.route ~> check {
        status shouldBe StatusCodes.OK
      }
    }
  }

  "/products/details/slug" should {

    "respond with NotFound when no product details with such slug exists" in new Scope {
      when(productService.productDetails(M.anyString())) thenReturn s"No product details found for $slug".ko.fe

      Get(s"/products/details/$slug") ~> service.route ~> check {
        status shouldBe StatusCodes.NotFound
        contentType shouldBe ContentTypes.`application/json`
        responseAs[Error] shouldBe Error(404, s"No product details found for $slug")
      }
    }

    "respond with OK when product slug is correct" in new Scope {
      when(productService.productDetails(slug)) thenReturn ProductDetailsRow("cool", "stuf").ok.fe[String]

      Get(s"/products/details/$slug") ~> service.route ~> check {
        status shouldBe StatusCodes.OK
      }
    }
  }

  "/products/slug" should {
    "respond with NotFound when no product with such slug exists" in new Scope {
      when(productService.product(M.anyString())) thenReturn s"Invalid product slug $slug".ko.fe

      Get(s"/products/$slug") ~> service.route ~> check {
        status shouldBe StatusCodes.NotFound
        contentType shouldBe ContentTypes.`application/json`
        responseAs[Error] shouldBe Error(404, s"Invalid product slug $slug")
      }
    }

    "respond with OK when product slug is correct" in new Scope {
      when(productService.product(slug)) thenReturn sweater.ok.fe[String]

      Get(s"/products/$slug") ~> service.route ~> check {
        status shouldBe StatusCodes.OK
      }
    }
  }

  trait Scope {
    val slug = "sweater"
    val id = 42

    val sweater = ProductRow(
      id = ProductId(0),
      name = "Gucci",
      title = "sweater",
      description = "nice and warm",
      categoryId = CategoryId(0),
      media = "just.url",
      price = 100500
    )

    val clothes = CategoryRow(
      id = CategoryId(0),
      name = "clothes",
      slug = "some stuff",
      title = "amazing",
      description = "not kidding"
    )

    val productService = mock[ProductService]
    val service = new ProductHttp(productService)
  }
}