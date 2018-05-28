package com.rtfmarket.domain

import com.rtfmarket.http.Data
import com.rtfmarket.slick.{CategoryId, CategoryRow}
import play.api.libs.json.Writes

case class Category(
  id: CategoryId,
  name: String,
  slug: String,
  title: String,
  description: String,
  products: Seq[Product],
  filters: Seq[Filter]
) extends Data[Category]

object Category {
  def apply(
    row: CategoryRow,
    products: Seq[Product] = List.empty,
    filters: Seq[Filter] = List.empty
  )(implicit writes: Writes[Category]): Category =

    Category(row.id, row.name, row.slug, row.title, row.description, products, filters)
}
