package com.rtfmarket.http

import akka.http.scaladsl.marshallers.playjson.PlayJsonSupport._
import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.evolutiongaming.util.Validation._
import com.rtfmarket.http.Error.ErrorResponseFormat
import com.rtfmarket.services.ProductServiceImpl
import com.rtfmarket.slick.CategoryId
import org.mockito.Mockito._
import org.mockito.{ArgumentMatchers => M}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Matchers, WordSpec}

class ProductHttpSpec extends WordSpec with Matchers with ScalatestRouteTest with MockitoSugar {
  "/products/category/id" should {
    "respond with NotFound no such category exists" in new Scope {
      val id = 228
      when(productService.category(CategoryId(M.any()))) thenReturn s"Invalid category id $id".ko.fe

      Get("/products/category/228") ~> service.route ~> check {
        status shouldBe StatusCodes.NotFound
        contentType shouldBe ContentTypes.`application/json`
        responseAs[Error] shouldBe Error(404, s"Invalid category id $id")
      }
    }
  }

  "/products/details/slug" should {
    "respond with OK for stub" in new Scope {
      Get("/products/details/slug") ~> service.route ~> check {
        status shouldBe StatusCodes.OK
      }
    }
  }

  "/products/slug" should {
    "respond with OK for stub" in new Scope {
      Get("/products/slug") ~> service.route ~> check {
        status shouldBe StatusCodes.OK
      }
    }
  }

  trait Scope {
    val productService = mock[ProductServiceImpl]
    val service = new ProductHttp(productService)
  }
}
