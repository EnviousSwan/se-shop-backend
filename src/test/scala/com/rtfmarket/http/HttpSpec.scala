package com.rtfmarket.http

import akka.http.scaladsl.model.{ContentTypes, StatusCode, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.marshallers.playjson.PlayJsonSupport._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Assertion, Matchers, WordSpec}

trait HttpSpec extends WordSpec with Matchers with ScalatestRouteTest with MockitoSugar {
  private def checkContentType() =
    contentType shouldBe ContentTypes.`application/json`

  def checkForError(statusCode: StatusCode, message: String): Assertion = {
    status shouldBe statusCode
    checkContentType()
    responseAs[Error] shouldBe Error(statusCode, message)
  }

  def checkForSuccess(just: Just): Assertion = {
    status shouldBe StatusCodes.OK
    checkContentType()
    responseAs[Just] shouldBe just
  }
}
