package com.rtfmarket.http

import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.marshallers.playjson.PlayJsonSupport._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.evolutiongaming.util.Validation._
import com.rtfmarket.services.UserService
import com.rtfmarket.slick.{UserId, UserRow}
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Matchers, WordSpec}
import UserHttp.{LoginRequest, userFormat}
import org.mockito.{ArgumentMatchers => M}

class UserHttpSpec extends HttpSpec {

  "/user" should {
    "respond with OK for stub" in new Scope {
      when(userService.createUser(user)) thenReturn ().ok.fe[String]

      Post("/user", user) ~> service.route ~> check {
        status shouldBe StatusCodes.OK
      }
    }

    "respond with BadRequest if user with such email already exists" in new Scope {
      when(userService.createUser(M.any())) thenReturn s"User already exists".ko.fe

      Post("/user", user) ~> service.route ~> check {
        checkForError(StatusCodes.BadRequest, "User already exists")
      }
    }
  }

  "/user/login" should {
    "respond with NotFound if no user with such email exists" in new Scope {
      when(userService.userExists(email)) thenReturn false.future

      Post("/user/login", loginRequest) ~> service.route ~> check {
        checkForError(StatusCodes.NotFound, s"No user found with email $email")
      }
    }

    "respond with BadRequest for incorrect password" in new Scope {
      when(userService.userExists(email)) thenReturn true.future
      when(userService.loginUser(email, password)) thenReturn "Incorrect password".ko.fe

      Post("/user/login", loginRequest) ~> service.route ~> check {
        checkForError(StatusCodes.BadRequest, "Incorrect password")
      }
    }

    "respond with OK if logged in successfully" in new Scope {
      when(userService.userExists(email)) thenReturn true.future
      when(userService.loginUser(email, password)) thenReturn ().ok.fe[String]

      Post("/user/login", loginRequest) ~> service.route ~> check {
        status shouldBe StatusCodes.OK
      }
    }
  }

  "/user/logout" should {
    "respond with OK for stub" in new Scope {
      Get("/user/logout") ~> service.route ~> check {
        status shouldBe StatusCodes.OK
      }
    }
  }

  "user/id" should {

    "respond with NotFound if no user with such email exists" in new Scope {
      when(userService.userExists(id)) thenReturn false.future

      Get(s"/user/${id.value}", loginRequest) ~> service.route ~> check {
        checkForError(StatusCodes.NotFound, s"No user found with id $id")
      }
    }

    "respond with OK for get stub" in new Scope {
      when(userService.userExists(id)) thenReturn true.future
      when(userService.user(id)) thenReturn user.ok.fe[String]

      Get(s"/user/${id.value}") ~> service.route ~> check {
        status shouldBe StatusCodes.OK
      }
    }

    "respond with NotFound when trying to update non-existing user" in new Scope {
      when(userService.userExists(id)) thenReturn false.future

      Put(s"/user/${id.value}", user) ~> service.route ~> check {
        checkForError(StatusCodes.NotFound, s"No user found with id $id")
      }
    }

    "respond with BadRequest when trying to update user incorrectly" in new Scope {
      when(userService.userExists(id)) thenReturn true.future
      when(userService.updateUser(user)) thenReturn s"User already exists".ko.fe

      Put(s"/user/${id.value}", user) ~> service.route ~> check {
        checkForError(StatusCodes.BadRequest, "User already exists")
      }
    }

    "respond with OK for put stub" in new Scope {
      when(userService.userExists(id)) thenReturn true.future
      when(userService.updateUser(user)) thenReturn ().ok.fe[String]

      Put(s"/user/${id.value}", user) ~> service.route ~> check {
        status shouldBe StatusCodes.OK
      }
    }

    "respond with OK for delete stub" in new Scope {
      Delete(s"/user/${id.value}") ~> service.route ~> check {
        status shouldBe StatusCodes.OK
      }
    }
  }

  trait Scope {
    val userService = mock[UserService]
    val service = new UserHttp(userService)

    val emailExists = "exist@mail.com"

    val id = UserId(0)
    val email = "example@mail.com"
    val password = "password"

    val user = UserRow(
      id = id,
      email = email,
      firstName = "John",
      lastName = "Doe",
      passwordSha = Some(password),
      phone = Some("+123424"),
      address = Some("NYC")
    )

    val loginRequest = LoginRequest(email, password)
  }
}
