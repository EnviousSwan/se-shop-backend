package com.rtfmarket.services

import com.evolutiongaming.util.Validation._
import com.rtfmarket.domain.{Cart, CartItem}
import slick.jdbc.H2Profile.api.{Database => _, _}
import com.rtfmarket.slick._
import play.api.libs.json.{Json, OFormat}

import scala.concurrent.{ExecutionContext, Future}

class CartServiceImpl(db: Database) extends CartService {

  def cart(userId: UserId): FV[Cart] = Cart(CartRow(CartId.Test, UserId.Test)).ok.fe[String]

  def addProduct(userId: UserId, product: Product): FV[Unit] = ().ok.fe[String]

  def removeProduct(userId: UserId, productId: ProductId): FV[Unit] = ().ok.fe[String]

  def changeProductQuantity(userId: UserId, productId: ProductId, quantity: Int): FV[Unit] = ().ok.fe[String]
}

object CartServiceImpl {

  import com.rtfmarket.http.IdFormats._
  import ProductServiceImpl.ProductFormat

  implicit val CartItemFormat: OFormat[CartItem] = Json.format[CartItem]
  implicit val CartFormat: OFormat[Cart] = Json.format[Cart]

  class Db(db: Database)(implicit executionContext: ExecutionContext) extends CartService.Db {
    def cart(userId: UserId): Future[Option[CartRow]] = ???
  }
}