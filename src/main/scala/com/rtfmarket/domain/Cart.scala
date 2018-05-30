package com.rtfmarket.domain

import com.rtfmarket.http.Data
import com.rtfmarket.slick.{CartId, CartRow, UserId}

case class CartItem(count: Int = 0, products: List[Product] = List(Product()))

case class Cart(items: List[CartItem] = List(CartItem())) extends Data[Cart]

object Cart {
  def apply(cartRow: CartRow): Cart = Cart()

  implicit class CartOps(val value: Cart) extends AnyVal {
    def toRow(userId: UserId): CartRow = toCartRow(value, userId)
  }

  def toCartRow(cart: Cart, userId: UserId): CartRow =
    CartRow(CartId.Test, UserId.Test)
}
