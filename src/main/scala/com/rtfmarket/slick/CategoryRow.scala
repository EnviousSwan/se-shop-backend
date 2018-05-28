package com.rtfmarket.slick

import slick.jdbc.H2Profile.api._

case class CategoryId(value: Long) extends MappedTo[Long]

object CategoryId {
  lazy val Test = CategoryId(0)
}

case class CategoryRow(
  id: CategoryId,
  name: String,
  slug: String,
  title: String,
  description: String)

final class Categories(tag: Tag) extends Table[CategoryRow](tag, "Category") {
  def id = column[CategoryId]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def slug = column[String]("slug")
  def title = column[String]("title")
  def description = column[String]("description")

  def * = (id, name, slug, title, description).mapTo[CategoryRow]
}

object Categories extends TableQuery(new Categories(_)) {
  lazy val bySlug = Compiled { slug: Rep[String] =>
    filter(_.slug === slug)
  }
}
