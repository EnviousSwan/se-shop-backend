package com.rtfmarket.slick

import slick.jdbc.H2Profile.api._

case class UserId(value: Long) extends AnyVal with MappedTo[Long]

object UserId {
  lazy val Default = UserId(0)
}

case class UserRow(
  id: UserId = UserId(0),
  email: String,
  firstName: String = "",
  lastName: String = "" ,
  passwordSha: Option[String] = None,
  phone: Option[String] = None,
  address: Option[String] = None
)

final class Users(tag: Tag) extends Table[UserRow](tag, "User") {
  def id = column[UserId]("id", O.PrimaryKey, O.AutoInc)
  def email = column[String]("email")
  def firstName = column[String]("first_name")
  def lastName = column[String]("last_name")
  def passwordSha = column[Option[String]]("pass_sha")
  def phone = column[Option[String]]("phone")
  def address = column[Option[String]]("address")

  override def * =
    (id, email, firstName, lastName, passwordSha, phone, address).mapTo[UserRow]
}

object Users extends TableQuery(new Users(_)) {

  def userByEmail(email: String) = Users.filter(_.email === email).result.headOption

  def userByPhone(phone: String) = Users.filter(_.phone === phone).result.headOption

  def createUser(user: UserRow): DBIO[UserId] =
    Users returning Users.map(_.id) += user
}
