package com.rtfmarket.http

import akka.http.scaladsl.model.StatusCodes
import com.evolutiongaming.util.Validation._
import com.rtfmarket.http.CartHttp.Cart
import com.rtfmarket.services.{CartService, ProductService}
import com.rtfmarket.slick._
import org.mockito.Mockito._
import org.mockito.{ArgumentMatchers => M}

class CartHttpSpec extends HttpSpec {
  "/cart" should {
    "respond with NotFound when no such cart exists" in new Scope {
      when(cartService.cart(UserId(M.anyLong()))) thenReturn "No cart found".ko.fe

      Get("/cart") ~> service.route ~> check {
        checkForError(StatusCodes.NotFound, "No cart found")
      }
    }

    "return products in user's cart" in new Scope {
      when(cartService.cart(UserId.Test)) thenReturn CartRow(CartId.Test, UserId.Test).ok.fe[String]

      Get("/cart") ~> service.route ~> check {
        checkForSuccess(Just(Cart()))
      }
    }
  }

  "/cart/product/slug" should {
    "respond with NotFound if no such product exists" in new Scope {
      when(productService.product(M.anyString())) thenReturn s"No product found".ko.fe

      Post(s"/cart/product/slug") ~> service.route ~> check {
        checkForError(StatusCodes.NotFound, "No product found")
      }
    }

    "add new product to cart" in new Scope {
      when(productService.product(slug)) thenReturn sweater.ok.fe[String]
      when(cartService.addProduct(UserId.Test, sweater)) thenReturn ().ok.fe[String]

      Post(s"/cart/product/$slug") ~> service.route ~> check {
        checkForSuccess(Just(StatusCodes.OK))
      }
    }

    "respond with NotFound if there's no such product" in new Scope {
      when(productService.product(M.anyString())) thenReturn "No product found".ko.fe

      Delete(s"/cart/product/other?count=5") ~> service.route ~> check {
        checkForError(StatusCodes.NotFound, "No product found")
      }
    }

    "respond with NotFound if no such product present in user's cart" ignore new Scope {}

    "remove product from cart" in new Scope {
      when(productService.product(slug)) thenReturn sweater.ok.fe[String]
      when(cartService.removeProduct(UserId.Test, sweater.id)) thenReturn ().ok.fe[String]

      Delete(s"/cart/product/$slug?count=1") ~> service.route ~> check {
        checkForSuccess(Just(StatusCodes.OK))
      }
    }

    "respond with NotFound if no product with such slug exists" in new Scope {
      when(productService.product("other")) thenReturn s"No product found".ko.fe

      Put(s"/cart/product/other?count=2") ~> service.route ~> check {
        checkForError(StatusCodes.NotFound, "No product found")
      }
    }

    "change quantity of product in user's cart" in new Scope {
      when(productService.product(slug)) thenReturn sweater.ok.fe[String]
      when(cartService.changeProductQuantity(UserId.Test, sweater.id, 2)) thenReturn ().ok.fe[String]

      Put(s"/cart/product/$slug?count=2") ~> service.route ~> check {
        checkForSuccess(Just(StatusCodes.OK))
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

