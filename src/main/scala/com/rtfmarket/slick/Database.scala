package com.rtfmarket.slick

import slick.dbio.{DBIOAction, NoStream}
import slick.jdbc.H2Profile
import slick.jdbc.H2Profile.api

import scala.concurrent.Future

class Database(db: api.Database) {
  def run[R](a: DBIOAction[R, NoStream, Nothing]): Future[R] =
    db run a
}

object Database {
  def forConfig(path: String) =
    new Database(api.Database forConfig path)

  type DB = H2Profile.backend.Database
}