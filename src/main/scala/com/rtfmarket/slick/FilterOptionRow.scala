package com.rtfmarket.slick

import slick.lifted.MappedTo
import slick.jdbc.H2Profile.api._

case class FilterOptionId(value: Long) extends AnyVal with MappedTo[Long]

object FilterOptionId {
  lazy val Test = FilterOptionId(0)
}

case class FilterOptionRow(
  id: FilterOptionId,
  name: String,
  title: String,
  filterId: FilterId)

final class FilterOptions(tag: Tag) extends Table[FilterOptionRow](tag, "FilterOption") {
  def id = column[FilterOptionId]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def title = column[String]("title")
  def filterId = column[FilterId]("filter_id")

  def * = (id, name, title, filterId).mapTo[FilterOptionRow]

  def filter = foreignKey("filter_fk", filterId, Filters)(_.id)
}

object FilterOptions extends TableQuery(new FilterOptions(_)) {

}
