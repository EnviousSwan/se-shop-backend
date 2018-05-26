package com.rtfmarket.slick

import com.rtfmarket.slick.Database.DB
import slick.dbio.{DBIOAction, NoStream}
import slick.jdbc.H2Profile

import scala.concurrent.Future

class Database(db: DB) {
  def run[R](a: DBIOAction[R, NoStream, Nothing]): Future[R] =
    db run a
}

object Database {
  type DB = H2Profile.backend.Database
}