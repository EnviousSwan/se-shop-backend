package com.rtfmarket.domain

import com.rtfmarket.http.Data

case class Order(items: List[CartItem] = List(CartItem())) extends Data[Order]

object Order {
  def apply(cart: Cart): Order =
    Order(cart.items)
}
