package com.rtfmarket.services

import com.evolutiongaming.util.Validation.FV
import com.rtfmarket.domain.{Category, ProductDetails, Product}
import com.rtfmarket.slick._

import scala.concurrent.Future

trait ProductService {

  def categories(): Future[Seq[Category]]

  def category(slug: String): FV[Category]

  def productDetails(id: ProductId): FV[ProductDetails]

  def product(id: ProductId): FV[Product]
}

object ProductService {
  trait Db {

    def categories(): Future[Seq[CategoryRow]]

    def category(slug: String): Future[Option[CategoryRow]]

    def productsByCategory(categoryId: CategoryId): Future[Seq[ProductRow]]

    def filtersByCategory(categoryId: CategoryId): Future[Seq[FilterRow]]

    def product(id: ProductId): Future[Option[ProductRow]]
  }
}
