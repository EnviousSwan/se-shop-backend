package com.rtfmarket.slick

import slick.lifted.MappedTo
import slick.jdbc.H2Profile.api._

case class PropertyValueId(value: Long) extends AnyVal with MappedTo[Long]

object PropertyValueId {
  lazy val Test = PropertyValueId(0)
}

case class PropertyValueRow(
  id: PropertyValueId,
  productId: ProductId,
  propertyId: PropertyId,
  value: String)

final class PropertyValues(tag: Tag)  extends Table[PropertyValueRow](tag, "PropertyValue") {
  def id = column[PropertyValueId]("id", O.PrimaryKey, O.AutoInc)
  def productId = column[ProductId]("product_id")
  def propertyId = column[PropertyId]("property_id")
  def value = column[String]("value")

  def * = (id, productId, propertyId, value).mapTo[PropertyValueRow]
}

object PropertyValues extends TableQuery(new PropertyValues(_))
