package com.rtfmarket.services

import com.evolutiongaming.util.FutureOption
import com.evolutiongaming.util.Validation._
import com.rtfmarket.domain._
import com.rtfmarket.slick._
import play.api.libs.json.{Json, OFormat}
import slick.jdbc.H2Profile.api.{Database => _, _}

import scala.concurrent.{ExecutionContext, Future}

class ProductServiceImpl(db: ProductServiceImpl.Db)
  (implicit executionContext: ExecutionContext) extends ProductService {

  override def categories(): Future[Seq[Category]] =
    db.categories() flatMap { rows =>
      Future sequence {
        rows map categoryFromRow
      }
    }

  def category(slug: String): FV[Category] =
    for {
      row <- db.category(slug) ?>> s"No category found with slug $slug"
      cat <- categoryFromRow(row).fe
    } yield cat

  def product(slug: String): FV[ProductRow] =
    ProductRow(ProductId.Test, "name", "title", "description", CategoryId.Test, "media", 100).ok.fe[String]

  def productDetails(slug: String): FV[ProductDetailsRow] =
    ProductDetailsRow("about", "properties").ok.fe[String]

  private def categoryFromRow(row: CategoryRow): Future[Category] =
    for {
      productRows <- db.productsByCategory(row.id)
      filterRows <- db.filtersByCategory(row.id)
      products = productRows map Product.apply
      filters = filterRows map Filter.apply
    } yield Category(row, products, filters)
}

object ProductServiceImpl {

  import com.rtfmarket.http.IdFormats._

  implicit val MediaFormat: OFormat[Media] = Json.format[Media]
  implicit val PropertyFormat: OFormat[ProductProperty] = Json.format[ProductProperty]
  implicit val FilterOptionFormat: OFormat[FilterOption] = Json.format[FilterOption]
  implicit val FilterFormat: OFormat[Filter] = Json.format[Filter]
  implicit val ProductFormat: OFormat[Product] = Json.format[Product]
  implicit val CategoryFormat: OFormat[Category] = Json.format[Category]

  implicit val PropertyOptionFormat: OFormat[ProductDetailsPropertyOption] = Json.format[ProductDetailsPropertyOption]
  implicit val DetailsPropertyFormat: OFormat[ProductDetailsProperty] = Json.format[ProductDetailsProperty]
  implicit val ReviewFormatFormat: OFormat[ProductReview] = Json.format[ProductReview]
  implicit val ProductDetailsFormat: OFormat[ProductDetails] = Json.format[ProductDetails]

  class Db(db: Database)(implicit executionContext: ExecutionContext) {

    def categories(): Future[Seq[CategoryRow]] = db run Categories.result

    def category(slug: String): FO[CategoryRow] = FutureOption {
      db run Categories.bySlug(slug).result.headOption
    }

    def productsByCategory(categoryId: CategoryId): Future[Seq[ProductRow]] =
      db run Products.byCategoryId(categoryId).result

    def filtersByCategory(categoryId: CategoryId): Future[Seq[FilterRow]] =
      db run Filters.byCategoryId(categoryId).result
  }
}
