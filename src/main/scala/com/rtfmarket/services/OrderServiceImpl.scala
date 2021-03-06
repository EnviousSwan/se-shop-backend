package com.rtfmarket.services

import com.evolutiongaming.util.Validation.FV
import com.rtfmarket.domain.Order
import com.rtfmarket.slick.{OrderId, OrderRow, UserId}

class OrderServiceImpl extends OrderService {

  def placeOrder(userId: UserId): FV[Unit] = ???

  def orders(userId: UserId): FV[List[Order]] = ???

  def order(orderId: OrderId): FV[Order] = ???
}
