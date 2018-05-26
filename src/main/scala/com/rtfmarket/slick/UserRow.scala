package com.rtfmarket.slick

import slick.jdbc.H2Profile.api._

case class UserId(value: Long) extends AnyVal with MappedTo[Long]

case class UserRow(
  id: UserId,
  email: String,
  firstName: String = "",
  lastName: String = "" ,
  passwordSha: Option[String] = None,
  phoneNumber: Option[String] = None,
  address: Option[String] = None
)

final class Users(tag: Tag) extends Table[UserRow](tag, "User") {
  def id = column[UserId]("id", O.PrimaryKey, O.AutoInc)
  def email = column[String]("email")
  def firstName = column[String]("first_name")
  def lastName = column[String]("last_name")
  def passwordSha = column[Option[String]]("pass_sha")
  def phoneNumber = column[Option[String]]("phone_number")
  def address = column[Option[String]]("address")

  override def * =
    (id, email, firstName, lastName, passwordSha, phoneNumber, address).mapTo[UserRow]
}

object Users extends TableQuery(new Users(_)) {

}
