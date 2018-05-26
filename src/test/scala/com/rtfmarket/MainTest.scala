package com.rtfmarket

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.rtfmarket.http.MainRoute
import org.scalatest.{Matchers, WordSpec}

class MainTest extends WordSpec with Matchers with ScalatestRouteTest {
  "The server" should {
    "respond with on OK on check" in {
      Get("/check") ~> MainRoute.route ~> check {
        status shouldBe StatusCodes.OK
      }
    }
  }
}
