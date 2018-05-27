package com.rtfmarket.services

import com.evolutiongaming.util.Validation._
import com.rtfmarket.slick._

import scala.concurrent.ExecutionContext

class ProductServiceImpl(db: Database)
  (implicit executionContext: ExecutionContext) extends ProductService {

  def category(categoryId: CategoryId): FV[CategoryRow] =
    CategoryRow(CategoryId.Default, "name", "slug", "title", "desc").ok.fe[String]

  def category(slug: String): FV[CategoryRow] =
    CategoryRow(CategoryId.Default, "name", "slug", "title", "desc").ok.fe[String]

  def productDetails(slug: String): FV[ProductDetailsRow] = ???

  def product(slug: String): FV[ProductRow] = ???
}
