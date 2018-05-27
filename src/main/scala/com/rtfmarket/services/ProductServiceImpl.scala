package com.rtfmarket.services

import com.evolutiongaming.util.Validation._
import com.rtfmarket.slick._

import scala.concurrent.ExecutionContext

class ProductServiceImpl(db: Database)
  (implicit executionContext: ExecutionContext) extends ProductService {

  def category(slug: String): FV[CategoryRow] =
    if (slug == "slug")
      CategoryRow(CategoryId.Default, "name", "slug", "title", "desc").ok.fe[String]
    else
      s"Category '$slug' not found".ko.fe

  def productDetails(slug: String): FV[ProductDetailsRow] =
    if (slug == "slug")
      ProductDetailsRow("about", "properties").ok.fe[String]
    else
      s"No product details found for '$slug'".ko.fe

  def product(slug: String): FV[ProductRow] =
    if (slug == "slug")
      ProductRow(ProductId.Default, "name", "title", "description", CategoryId.Default, "media", 100).ok.fe[String]
    else
      s"No product found for '$slug'".ko.fe
}
