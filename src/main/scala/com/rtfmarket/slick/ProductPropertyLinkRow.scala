package com.rtfmarket.slick

import slick.jdbc.H2Profile.api._

case class ProductPropertyLinkRow(productId: ProductId, propertyId: PropertyId)

final class ProductPropertyLinks(tag: Tag) extends Table[ProductPropertyLinkRow](tag, "product_property_map") {
  def productId = column[ProductId]("product_id")
  def propertyId = column[PropertyId]("property_id")

  def * = (productId, propertyId).mapTo[ProductPropertyLinkRow]

  def product = foreignKey("product_fk", productId, Products)(_.id)
  def property = foreignKey("property_fk", propertyId, Properties)(_.id)
}

object ProductPropertyLinks extends TableQuery(new ProductPropertyLinks(_)) {

  lazy val byProductId = Compiled { id: Rep[ProductId] => filter(_.productId === id) }

  lazy val byPropertyId = Compiled { id: Rep[PropertyId] => filter(_.propertyId === id) }
}