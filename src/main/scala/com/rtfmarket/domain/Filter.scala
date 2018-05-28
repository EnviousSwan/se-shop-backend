package com.rtfmarket.domain

import com.rtfmarket.slick.{FilterId, FilterRow}
import play.api.libs.json.{Json, OFormat}

case class FilterOption(
  id: Long = 0,
  name: String = "name",
  title: String = "title")

object FilterOption {
  implicit val Format: OFormat[FilterOption] = Json.format[FilterOption]
}

case class Filter(
  id: FilterId = FilterId.Test,
  name: String = "name",
  title: String = "title",
  description: String = "description",
  multiple: Boolean = false,
  options: List[FilterOption] = List(FilterOption()))

object Filter {
  def apply(row: FilterRow): Filter =
    Filter(
      id = row.id,
      name = row.name,
      title = row.title,
      description = row.description,
      multiple = row.multiple,
      options = List())
}