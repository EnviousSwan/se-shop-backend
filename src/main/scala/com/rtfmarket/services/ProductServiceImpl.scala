package com.rtfmarket.services

import com.evolutiongaming.util.Validation._
import com.rtfmarket.slick._

import scala.concurrent.ExecutionContext

class ProductServiceImpl(db: Database)
  (implicit executionContext: ExecutionContext) extends ProductService {

  def category(slug: String): FV[CategoryRow] =
    CategoryRow(CategoryId.Default, "name", "slug", "title", "desc").ok.fe[String]

  def productDetails(slug: String): FV[ProductDetailsRow] =
    ProductDetailsRow("about", "properties").ok.fe[String]

  def product(slug: String): FV[ProductRow] =
    ProductRow(ProductId.Default, "name", "title", "description", CategoryId.Default, "media", 100).ok.fe[String]

}
