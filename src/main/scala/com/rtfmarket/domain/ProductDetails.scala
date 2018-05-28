package com.rtfmarket.domain

import com.rtfmarket.http.Data
import com.rtfmarket.slick.ProductDetailsRow
import play.api.libs.json.{Json, OFormat}

case class ProductDetailsPropertyOption(
  id: Long = 0,
  name: String = "name",
  title: String = "title")

case class ProductDetailsProperty(
  id: Long = 0,
  name: String = "name",
  title: String = "title",
  description: String = "description",
  multiple: Boolean = false,
  options: List[ProductDetailsPropertyOption] = List(ProductDetailsPropertyOption()))

case class ProductReview(
  name: String = "name",
  media: Media = Media(),
  rating: Double = 4.0,
  content: String = "content")

case class ProductDetails(
  about: String = "about",
  properties: List[ProductDetailsProperty] = List(ProductDetailsProperty()),
  rating: Double = 4.5,
  reviews: List[ProductReview] = List(ProductReview())
) extends Data[ProductDetails]

object ProductDetails {
  def apply(productDetailsRow: ProductDetailsRow): ProductDetails =
    ProductDetails(productDetailsRow.about)
}