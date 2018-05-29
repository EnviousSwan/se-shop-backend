package com.rtfmarket.services

import com.rtfmarket.slick._
import org.scalatest.{Matchers, WordSpec}
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import com.evolutiongaming.util.Validation._
import com.rtfmarket.domain.{Category, Filter, Product}
import org.scalatest.concurrent.ScalaFutures
import org.mockito.{ArgumentMatchers => M}
import com.evolutiongaming.concurrent.CurrentThreadExecutionContext
import com.evolutiongaming.util.FutureOption

import scala.concurrent.ExecutionContext

class ProductServiceSpec extends WordSpec with Matchers with MockitoSugar with ScalaFutures {

  implicit val executionContext: ExecutionContext = CurrentThreadExecutionContext

  "Product service" should {

    "return list of categories" in new Scope {
      when(db.categories()) thenReturn List(categoryRow).future
      when(db.productsByCategory(categoryRow.id)) thenReturn List(productRow).future
      when(db.filtersByCategory(categoryRow.id)) thenReturn List(filterRow).future

      service.categories().futureValue shouldBe List(category)
    }

    "return an error if no category found by slug" in new Scope {
      when(db.category(M.anyString())) thenReturn FutureOption.empty

      service.category("other").future.futureValue shouldBe "No category found with slug other".ko
    }

    "return category by slug" in new Scope {
      when(db.category(slug)) thenReturn categoryRow.fo
      when(db.productsByCategory(categoryRow.id)) thenReturn List(productRow).future
      when(db.filtersByCategory(categoryRow.id)) thenReturn List(filterRow).future

      service.category(slug).future.futureValue shouldBe category.ok[String]
    }
  }

  trait Scope {

    val slug = "stuff"

    val categoryRow = CategoryRow(
      id = CategoryId.Test,
      name = "clothes",
      slug = slug,
      title = "amazing",
      description = "not kidding"
    )

    val productRow = ProductRow(
      id = ProductId.Test,
      name = "name",
      title = "title",
      description = "description",
      categoryId = categoryRow.id,
      media = "link.url",
      price = 1000
    )
    val product = Product(productRow)

    val filterRow = FilterRow(
      id = FilterId.Test,
      name = "name",
      title = "title",
      description = "description",
      multiple = true,
      categoryId = productRow.categoryId
    )
    val filter = Filter(filterRow)

    val category = Category(categoryRow, List(product), List(filter))

    val db = mock[ProductServiceImpl.Db]
    val service = new ProductServiceImpl(db)
  }
}
