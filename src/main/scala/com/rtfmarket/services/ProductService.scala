package com.rtfmarket.services

import com.evolutiongaming.util.Validation.FV
import com.rtfmarket.domain.Category
import com.rtfmarket.slick.{ProductDetailsRow, ProductRow}

import scala.concurrent.Future

trait ProductService {

  def categories(): Future[Seq[Category]]

  def category(slug: String): FV[Category]

  def productDetails(slug: String): FV[ProductDetailsRow]

  def product(slug: String): FV[ProductRow]
}
