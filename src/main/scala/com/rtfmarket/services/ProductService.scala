package com.rtfmarket.services

import com.evolutiongaming.util.Validation.FV
import com.rtfmarket.slick.CategoryId

trait ProductService {
  def category(categoryId: CategoryId): FV[CategoryId]
}
