package com.rtfmarket.services

import com.evolutiongaming.util.Validation._
import com.rtfmarket.slick._

class CartServiceImpl(db: Database) extends CartService {

  def cart(userId: UserId): FV[CartRow] = CartRow(CartId.Test, UserId.Test).ok.fe[String]

  def addProduct(userId: UserId, productRow: ProductRow): FV[Unit] = ().ok.fe[String]

  def removeProduct(userId: UserId, productId: ProductId): FV[Unit] = ().ok.fe[String]

  def changeProductQuantity(userId: UserId, productId: ProductId, quantity: Int): FV[Unit] = ().ok.fe[String]
}
