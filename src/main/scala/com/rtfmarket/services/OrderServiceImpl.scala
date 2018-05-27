package com.rtfmarket.services

import com.evolutiongaming.util.Validation.FV
import com.rtfmarket.slick.{OrderId, OrderRow, UserId}

class OrderServiceImpl extends OrderService {

  def placeOrder(userId: UserId, orderRow: OrderRow): FV[Unit] = ???

  def orders(userId: UserId): FV[List[OrderRow]] = ???

  def order(orderId: OrderId): FV[OrderRow] = ???
}
