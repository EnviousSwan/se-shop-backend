package com.rtfmarket.slick

import slick.lifted.MappedTo
import slick.jdbc.H2Profile.api._

case class ProductId(value: Long) extends AnyVal with MappedTo[Long]

object ProductId {
  lazy val Test = ProductId(0)
}

case class ProductRow(
  id: ProductId,
  name: String,
  title: String,
  description: String,
  categoryId: CategoryId,
  media: String,
  price: Double)

final class Products(tag: Tag) extends Table[ProductRow](tag, "Product") {
  def id = column[ProductId]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def title = column[String]("title")
  def description = column[String]("description")
  def categoryId = column[CategoryId]("category_id")
  def media = column[String]("media")
  def price = column[Double]("price")

  def * = (id, name, title, description, categoryId, media, price).mapTo[ProductRow]

  def category = foreignKey("product_category_fk", categoryId, Categories)(_.id)
}

object Products extends TableQuery(new Products(_)) {
  lazy val byCategoryId = Compiled { categoryId: Rep[CategoryId] =>
    filter(_.categoryId === categoryId)
  }

  lazy val byId = Compiled { id: Rep[ProductId] => filter(_.id === id) }
}
