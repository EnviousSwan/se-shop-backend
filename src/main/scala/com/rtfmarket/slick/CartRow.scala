package com.rtfmarket.slick

import slick.jdbc.H2Profile.api._

case class CartId(value: Long) extends AnyVal with MappedTo[Long]

case class CartRow(id: CartId, userId: UserId)

final class Carts(tag: Tag) extends Table[CartRow](tag, "Cart") {
  def id = column[CartId]("id", O.PrimaryKey, O.AutoInc)
  def userId = column[UserId]("user_id")

  def * = (id, userId).mapTo[CartRow]

  def user = foreignKey("user_fk", userId, Users)(_.id)
}

object Carts extends TableQuery(new Carts(_)) {

}
