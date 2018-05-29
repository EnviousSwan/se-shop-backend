package com.rtfmarket.slick

import slick.lifted.MappedTo
import slick.jdbc.H2Profile.api._

case class FilterId(value: Long) extends AnyVal with MappedTo[Long]

object FilterId {
  lazy val Test = FilterId(0)
}

case class FilterRow(
  id: FilterId,
  name: String,
  title: String,
  description: String,
  multiple: Boolean,
  categoryId: CategoryId)

final class Filters(tag: Tag) extends Table[FilterRow](tag, "Filter") {
  def id = column[FilterId]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def title = column[String]("title")
  def description = column[String]("description")
  def multiple = column[Boolean]("multiple")
  def categoryId = column[CategoryId]("category_id")

  def * = (id, name, title, description, multiple, categoryId).mapTo[FilterRow]

  def category = foreignKey("filter_category_fk", categoryId, Categories)(_.id)
}

object Filters extends TableQuery(new Filters(_)) {
  lazy val byCategoryId = Compiled { categoryId: Rep[CategoryId] =>
    filter(_.categoryId === categoryId)
  }
}
