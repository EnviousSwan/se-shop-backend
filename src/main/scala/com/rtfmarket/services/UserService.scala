package com.rtfmarket.services

import com.rtfmarket.slick.UserId

trait UserService {

  def createUser: Int

  def loginUser(email: String, password: String): Int

  def logoutUser(email: String): Int

  def deleteUser(userId: UserId): Int

  def updateUser: Int
}
