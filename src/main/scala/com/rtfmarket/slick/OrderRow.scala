package com.rtfmarket.slick

import slick.jdbc.H2Profile.api._

case class OrderId(value: Long) extends AnyVal with MappedTo[Long]

object OrderId {
  val Default = OrderId(0)
}

case class OrderRow(id: OrderId, userId: UserId, cartId: CartId)

final class Orders(tag: Tag) extends Table[OrderRow](tag, "Order") {
  def id = column[OrderId]("id", O.PrimaryKey, O.AutoInc)
  def userId = column[UserId]("user_id")
  def cartId = column[CartId]("cart_id")

  def * = (id, userId, cartId).mapTo[OrderRow]

  def user = foreignKey("user_fk", userId, Users)(_.id)
  def cart = foreignKey("cart_fk", cartId, Carts)(_.id)
}

object Orders extends TableQuery(new Orders(_)) {

}