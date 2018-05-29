package com.rtfmarket.services

import com.evolutiongaming.util.Validation._
import com.rtfmarket.domain.User
import com.rtfmarket.slick._
import play.api.libs.json.{Json, OFormat}
import slick.jdbc.H2Profile.api.{Database => _, _}

import scala.concurrent.{ExecutionContext, Future}

class UserServiceImpl(db: UserService.Db)
  (implicit executionContext: ExecutionContext) extends UserService {

  def userExists(email: String): Future[Boolean] = db.userExists(email)

  def userExists(id: UserId): Future[Boolean] = db.userExists(id)

  def createUser(user: User): FV[Unit] =
    for {
      _ <- userExists(user.email).map { _ falseOr s"User with email ${ user.email } already exists" }.fe
      _ <- db.insertUser(user.toRow).fe
    } yield ()

  def loginUser(email: String, password: String): FV[Unit] =
    for {
      user <- findUser(email)
      _ <- (user.password == password trueOr "Invalid credentials").fe
    } yield ()

  def logoutUser(id: UserId): FV[Unit] =
    userExists(id).map { _ trueOr s"No user found with id ${ id.value }" }.fe

  def findUser(id: UserId): FV[User] =
    db.user(id).fo toRight s"No user found with id ${ id.value }" map User.apply

  def findUser(email: String): FV[User] =
    db.user(email).fo toRight s"No user found with email $email" map User.apply

  def deleteUser(id: UserId): FV[Unit] =
    for {
      _ <- userExists(id).map { _ trueOr s"No user found with id ${ id.value }" }.fe
      _ <- db.deleteUser(id).fe
    } yield ()

  def updateUser(user: User): FV[Unit] =
    for {
      u <- findUser(user.id)
      _ <- (u.email == user.email trueOr "User with such email already exists").fe
      _ <- db.updateUser(user.toRow).fe
    } yield ()
}

object UserServiceImpl {

  import com.rtfmarket.http.IdFormats._

  implicit val UserFormat: OFormat[User] = Json.format[User]

  class Db(db: Database)(implicit executionContext: ExecutionContext) extends UserService.Db {

    def insertUser(user: UserRow): Future[Int] = db run (Users += user)

    def userExists(email: String): Future[Boolean] = db run Users.existsByEmail(email).result

    def userExists(id: UserId): Future[Boolean] = db run Users.existsById(id).result

    def user(id: UserId): Future[Option[UserRow]] = db run Users.byId(id).result.headOption

    def user(email: String): Future[Option[UserRow]] = db run Users.byEmail(email).result.headOption

    def deleteUser(id: UserId): Future[Int] = db run Users.byId(id).delete

    def updateUser(user: UserRow): Future[Int] = db run Users.byId(user.id).update(user)
  }
}
