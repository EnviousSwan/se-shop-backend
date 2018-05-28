package com.rtfmarket.slick

import slick.lifted.MappedTo
import slick.jdbc.H2Profile.api._

case class PropertyId(value: Long) extends AnyVal with MappedTo[Long]

object PropertyId {
  lazy val Test = PropertyId(0)
}

case class PropertyRow(
  id: PropertyId,
  name: String,
  title: String
)

final class Properties(tag: Tag) extends Table[PropertyRow](tag, "Category") {
  def id = column[PropertyId]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def title = column[String]("title")

  def * = (id, name, title).mapTo[PropertyRow]
}

object Properties extends TableQuery(new Properties(_))