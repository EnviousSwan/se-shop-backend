package com.rtfmarket.http

import akka.http.scaladsl.model.StatusCodes
import com.evolutiongaming.util.Validation._
import com.rtfmarket.http.ProductHttp.{Category, Product, ProductDetails}
import com.rtfmarket.services.ProductService
import com.rtfmarket.slick._
import org.mockito.Mockito._
import org.mockito.{ArgumentMatchers => M}

class ProductHttpSpec extends HttpSpec {

  "/products/category/id" should {
    "respond with NotFound no when such category exists" in new Scope {
      when(productService.category(M.anyString())) thenReturn s"Category not found".ko.fe

      Get(s"/products/category/id") ~> service.route ~> check {
        checkForError(StatusCodes.NotFound, "Category not found")
      }
    }

    "respond with OK when category with such id exists" in new Scope {
      when(productService.category(clothes.slug)) thenReturn clothes.ok.fe[String]

      Get(s"/products/category/${clothes.slug}") ~> service.route ~> check {
        checkForSuccess(Just(Category(clothes)))
      }
    }
  }

  "/products/slug/details" should {

    "respond with NotFound when no product details with such slug exists" in new Scope {
      when(productService.productDetails(M.anyString())) thenReturn s"No product details found".ko.fe

      Get(s"/products/slug/details") ~> service.route ~> check {
        checkForError(StatusCodes.NotFound, "No product details found")
      }
    }

    "respond with OK when product slug is correct" in new Scope {
      when(productService.productDetails(slug)) thenReturn details.ok.fe[String]

      Get(s"/products/$slug/details") ~> service.route ~> check {
        checkForSuccess(Just(ProductDetails(details)))
      }
    }
  }

  "/products/slug" should {
    "respond with NotFound when no product with such slug exists" in new Scope {
      when(productService.product(M.anyString())) thenReturn s"No product found".ko.fe

      Get(s"/products/slug") ~> service.route ~> check {
        checkForError(StatusCodes.NotFound, "No product found")
      }
    }

    "respond with OK when product slug is correct" in new Scope {
      when(productService.product(slug)) thenReturn sweater.ok.fe[String]

      Get(s"/products/$slug") ~> service.route ~> check {
        checkForSuccess(Just(Product(sweater)))
      }
    }
  }

  trait Scope {
    val slug = "sweater"
    val id = 42

    val clothes = CategoryRow(
      id = CategoryId.Default,
      name = "clothes",
      slug = "stuff",
      title = "amazing",
      description = "not kidding"
    )

    val sweater = ProductRow(
      id = ProductId.Default,
      name = "Gucci",
      title = "sweater",
      description = "nice and warm",
      categoryId = CategoryId(0),
      media = "just.url",
      price = 100500
    )

    val details = ProductDetailsRow("cool", "stuff")

    val productService: ProductService = mock[ProductService]
    val service = new ProductHttp(productService)
  }
}
