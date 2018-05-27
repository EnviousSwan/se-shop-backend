package com.rtfmarket.services

import com.evolutiongaming.util.Validation.FV
import com.rtfmarket.slick.{CartRow, ProductId, ProductRow, UserId}

trait CartService {

  def cart(userId: UserId): FV[CartRow]

  def addProduct(userId: UserId, productRow: ProductRow): FV[Unit]

  def removeProduct(userId: UserId, productId: ProductId): FV[Unit]

  def changeProductQuantity(userId: UserId, productId: ProductId, quantity: Int): FV[Unit]
}
