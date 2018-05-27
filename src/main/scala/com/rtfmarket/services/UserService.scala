package com.rtfmarket.services

import com.evolutiongaming.util.Validation.FV
import com.rtfmarket.slick.{UserId, UserRow}

import scala.concurrent.Future

trait UserService {

  def createUser(userRow: UserRow): FV[Unit]

  def userExists(email: String): Future[Boolean]

  def userExists(id: UserId): Future[Boolean]

  def loginUser(email: String, password: String): FV[Unit]

  def logoutUser(email: String): FV[Unit]

  def user(email: String): FV[UserRow]

  def user(id: UserId): FV[UserRow]

  def deleteUser(userId: UserId): FV[Unit]

  def updateUser(userRow: UserRow): FV[Unit]
}
