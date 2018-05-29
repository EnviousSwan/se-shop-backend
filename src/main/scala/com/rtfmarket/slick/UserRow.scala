package com.rtfmarket.slick

import slick.jdbc.H2Profile.api._

case class UserId(value: Long) extends AnyVal with MappedTo[Long]

object UserId {
  lazy val Test = UserId(0)
}

case class UserRow(
  id: UserId = UserId(0),
  email: String,
  firstName: String = "",
  lastName: String = "" ,
  password: String = "",
  phone: String = "",
  address: String = ""
)

final class Users(tag: Tag) extends Table[UserRow](tag, "User") {
  def id = column[UserId]("id", O.PrimaryKey, O.AutoInc)
  def email = column[String]("email")
  def firstName = column[String]("first_name")
  def lastName = column[String]("last_name")
  def passwordSha = column[String]("pass_sha")
  def phone = column[String]("phone")
  def address = column[String]("address")

  def * = (id, email, firstName, lastName, passwordSha, phone, address).mapTo[UserRow]
}

object Users extends TableQuery(new Users(_)) {

  lazy val byId = Compiled { id: Rep[UserId] => filter(_.id === id) }

  lazy val byEmail = Compiled { email: Rep[String] => filter(_.email === email) }

  lazy val existsById = Compiled { id: Rep[UserId] => filter(_.id === id).length > 0 }

  lazy val existsByEmail = Compiled { email: Rep[String] => filter(_.email === email).length > 0 }
}
