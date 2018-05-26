package com.rtfmarket.services

import com.evolutiongaming.util.Validation.FV
import com.rtfmarket.slick._

import scala.concurrent.ExecutionContext

class ProductServiceImpl(db: Database)
  (implicit executionContext: ExecutionContext) extends ProductService {

  def category(categoryId: CategoryId): FV[CategoryRow] = ???

  def productDetails(slug: String): FV[ProductDetailsRow] = ???

  def product(slug: String): FV[ProductRow] = ???
}
