package com.rtfmarket.services

import com.evolutiongaming.util.Validation.FV
import com.rtfmarket.domain.User
import com.rtfmarket.slick.{UserId, UserRow}

import scala.concurrent.Future

trait UserService {

  def createUser(user: User): FV[Unit]

  def userExists(email: String): Future[Boolean]

  def userExists(id: UserId): Future[Boolean]

  def loginUser(email: String, password: String): FV[Unit]

  def logoutUser(userId: UserId): FV[Unit]

  def findUser(email: String): FV[User]

  def findUser(id: UserId): FV[User]

  def deleteUser(userId: UserId): FV[Unit]

  def updateUser(user: User): FV[Unit]
}

object UserService {
  trait Db {

    def insertUser(user: UserRow): Future[Int]

    def userExists(email: String): Future[Boolean]

    def userExists(id: UserId): Future[Boolean]

    def deleteUser(id: UserId): Future[Int]

    def updateUser(user: UserRow): Future[Int]

    def user(id: UserId): Future[Option[UserRow]]

    def user(email: String): Future[Option[UserRow]]
  }
}