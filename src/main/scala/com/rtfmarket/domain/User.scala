package com.rtfmarket.domain

import com.rtfmarket.http.Data
import com.rtfmarket.slick.{UserId, UserRow}

case class User(
  id: UserId,
  email: String,
  firstName: String,
  lastName: String,
  password: String,
  phone: String,
  address: String) extends Data[User]

object User {
  def apply(userRow: UserRow): User =
    User(
      userRow.id,
      userRow.email,
      userRow.firstName,
      userRow.lastName,
      userRow.passwordMd,
      userRow.phone,
      userRow.address)

  implicit class UserOps(val value: User) extends AnyVal {
    def toRow: UserRow = toUserRow(value)
  }

  def toUserRow(user: User): UserRow =
    UserRow(
      user.id,
      user.email,
      user.firstName,
      user.lastName,
      user.password,
      user.phone,
      user.address)
}
