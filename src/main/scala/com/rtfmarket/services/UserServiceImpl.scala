package com.rtfmarket.services
import com.evolutiongaming.util.FutureEither
import com.evolutiongaming.util.Validation._
import com.rtfmarket.slick.{Database, UserId, Users}

import scala.concurrent.ExecutionContext

class UserServiceImpl(db: Database)
  (implicit executionContext: ExecutionContext) extends UserService {

  override def createUser: Int = ???

  override def loginUser(email: String, password: String): Int = ???

  override def logoutUser(email: String): Int = ???

  override def deleteUser(userId: UserId): Int = ???

  override def updateUser: Int = ???

  private def validatePhone(phone: String): FV[Unit] = ().ok.fe[String]

  private def validateEmail(email: String): FV[Unit] = FutureEither {
    db run Users.userByEmail(email) map {
      case None => ().ok
      case Some(_) => "User with such email already exists".ko
    }
  }
}
