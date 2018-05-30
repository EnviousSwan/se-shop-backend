package com.rtfmarket.domain

import com.rtfmarket.http.Data
import com.rtfmarket.slick._

case class Media(
  main: String = "http://via.placeholder.com/300",
  fallback: String = "http://via.placeholder.com/300")

case class ProductProperty(
  id: Long = 0,
  name: String = "name",
  title: String = "title")

case class Product(
  id: ProductId = ProductId.Test,
  name: String = "name",
  slug: String = "slug",
  title: String = "title",
  description: String = "description",
  categoryId: CategoryId = CategoryId.Test,
  media: Media = Media(),
  price: Double = 100,
  properties: List[ProductProperty] = List(ProductProperty()),
  inCart: Boolean = false,
  inFavourite: Boolean = false,
  createDate: String = "1970-01-01"
) extends Data[Product]

object Product {
  def apply(productRow: ProductRow): Product =
    Product(
      productRow.id,
      productRow.name,
      productRow.slug,
      productRow.title,
      productRow.description,
      productRow.categoryId)

  implicit class ProductOps(val value: Product) extends AnyVal {
    def toRow: ProductRow = toProductRow(value)
  }

  def toProductRow(product: Product): ProductRow =
    ProductRow(
      product.id,
      product.name,
      product.title,
      product.slug,
      product.description,
      product.categoryId,
      product.media.main,
      product.price)
}
