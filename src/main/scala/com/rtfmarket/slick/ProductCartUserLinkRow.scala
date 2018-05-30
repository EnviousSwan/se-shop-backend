package com.rtfmarket.slick

import slick.jdbc.H2Profile.api._

case class ProductCartUserLinkRow(productId: ProductId, cartId: CartId, userId: UserId)

final class ProductCartUserLinks(tag: Tag) extends Table[ProductCartUserLinkRow](tag, "product_cart_map") {
  def productId = column[ProductId]("product_id")
  def cartId = column[CartId]("cart_id")
  def userId = column[UserId]("user_id")

  def * = (productId, cartId, userId).mapTo[ProductCartUserLinkRow]

  def product = foreignKey("product_cart_fk", productId, Products)(_.id)
  def cart = foreignKey("cart_product_fk", cartId, Carts)(_.id)
  def user = foreignKey("user_cart_fk", userId, Users)(_.id)
}

object ProductCartUserLinks extends TableQuery(new ProductCartUserLinks(_)) {

  lazy val byUserId = Compiled { id: Rep[UserId] => filter(_.userId === id) }

  lazy val byCartId = Compiled { id: Rep[CartId] => filter(_.cartId === id) }

  lazy val byProductId = Compiled { id: Rep[ProductId] => filter(_.productId === id) }
}

