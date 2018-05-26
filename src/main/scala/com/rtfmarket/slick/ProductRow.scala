package com.rtfmarket.slick

import slick.lifted.MappedTo
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future

case class ProductId(value: Long) extends AnyVal with MappedTo[Long]

case class ProductRow(
  id: ProductId,
  name: String,
  title: String,
  description: String,
  categoryId: CategoryId,
  media: String,
  price: Double) {

  def properties: String = ???
  def inCart(userId: UserId): Future[Boolean] = ???
  def favourite(userId: UserId): Future[Boolean] = ???
}

final class Products(tag: Tag) extends Table[ProductRow](tag, "Product") {
  def id = column[ProductId]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def title = column[String]("tite")
  def description = column[String]("description")
  def categoryId = column[CategoryId]("category_id")
  def media = column[String]("media")
  def price = column[Double]("price")

  def * = (id, name, title, description, categoryId, media, price).mapTo[ProductRow]
}

object Products extends TableQuery(new Products(_))
