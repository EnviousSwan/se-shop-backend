package com.rtfmarket.services

import com.evolutiongaming.util.Validation.FV
import com.rtfmarket.domain.Cart
import com.rtfmarket.slick.{CartRow, ProductId, UserId}

import scala.concurrent.Future

trait CartService {

  def cart(userId: UserId): FV[Cart]

  def addProduct(userId: UserId, product: Product): FV[Unit]

  def removeProduct(userId: UserId, productId: ProductId): FV[Unit]

  def changeProductQuantity(userId: UserId, productId: ProductId, quantity: Int): FV[Unit]
}

object CartService {
  trait Db {
    def cart(userId: UserId): Future[Option[CartRow]]
  }
}