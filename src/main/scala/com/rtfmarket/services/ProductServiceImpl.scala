package com.rtfmarket.services

import com.evolutiongaming.util.Validation.FV
import com.rtfmarket.slick.{CategoryId, Database}

import scala.concurrent.ExecutionContext

class ProductServiceImpl(db: Database)
  (implicit executionContext: ExecutionContext) extends ProductService {

  def category(categoryId: CategoryId): FV[CategoryId] = ???
}
