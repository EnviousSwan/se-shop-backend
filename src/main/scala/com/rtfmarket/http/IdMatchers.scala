package com.rtfmarket.http

import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.server.PathMatcher.{Matched, Matching, Unmatched}
import akka.http.scaladsl.server.PathMatcher1
import com.rtfmarket.slick.CategoryId

import scala.reflect.ClassTag
import scala.util.control.NonFatal

object IdMatchers {
  type LongToId[ID] = Long => ID

  implicit val CategoryIdCtor: LongToId[CategoryId] = CategoryId.apply

  case class Id[ID : ClassTag]()(implicit ctor: Long => ID) extends PathMatcher1[ID] {
    def apply(path: Path): Matching[Tuple1[ID]] = path match {
      case Path.Segment(segment, tail) =>
        try Matched(tail, Tuple1(ctor(segment.toLong))) catch {
          case NonFatal(_) => Unmatched
        }
      case _ =>
        Unmatched
    }
  }
}
