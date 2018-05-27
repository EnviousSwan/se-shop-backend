package com.rtfmarket.services
import com.evolutiongaming.util.Validation.{FV, _}
import com.rtfmarket.slick.{Database, UserId, UserRow}

import scala.concurrent.{ExecutionContext, Future}

class UserServiceImpl(db: Database)
  (implicit executionContext: ExecutionContext) extends UserService {

  def createUser(userRow: UserRow): FV[Unit] = ???

  def loginUser(email: String, password: String): FV[Unit] = ???

  def userExists(email: String): Future[Boolean] = ???

  def userExists(id: UserId): Future[Boolean] = ???

  def logoutUser(email: String): FV[Unit] = ???

  def user(id: UserId): FV[UserRow] = ???

  def user(email: String): FV[UserRow] = ???

  def deleteUser(userId: UserId): FV[Unit] = ???

  def updateUser(userRow: UserRow): FV[Unit] = ???

  private def validatePhone(phone: String): FV[Unit] = ().ok.fe[String]

  private def validateEmail(email: String): FV[Unit] = ().ok.fe[String]
}
