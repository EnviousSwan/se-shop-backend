package com.rtfmarket.services

import com.evolutiongaming.util.Validation.FV
import com.rtfmarket.slick.{CategoryId, CategoryRow, ProductDetailsRow, ProductRow}

trait ProductService {

  def category(categoryId: CategoryId): FV[CategoryRow]

  def productDetails(slug: String): FV[ProductDetailsRow]

  def product(slug: String): FV[ProductRow]
}
