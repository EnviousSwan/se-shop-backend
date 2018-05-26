package com.rtfmarket.services

import com.evolutiongaming.util.FutureEither
import com.evolutiongaming.util.Validation._
import com.rtfmarket.slick.{Database, UserId, UserRow, Users}

import scala.concurrent.{ExecutionContext, Future}

case class RegistrationRequest(
  email: String,
  password: String,
  firstName: String,
  lastName: String,
  phone: String
)

class PlayerAuthService(db: Database)(implicit ec: ExecutionContext) {

  def registerUser(request: RegistrationRequest): FV[UserId] = {
    for {
      _ <- validateEmail(request.email)
      userId <- createUser(request)
    } yield userId
  }

  private def createUser(request: RegistrationRequest): FV[UserId] = FutureEither {
    for {
      userId <- db.run(Users.createUser(
        UserRow(
          id = UserId(0L),
          request.email,
          request.firstName,
          request.lastName,
          Some(request.password),
          Some(request.phone),
          Some("address")
        )
      ))
    } yield userId.ok[String]
  }

  private def validateEmail(email: String): FV[Unit] = FutureEither {
    db run Users.userByEmail(email) map {
      case None => ().ok
      case Some(_) => "User with such email already exists".ko
    }
  }
}
