package com.xpacked.swole.db

import scalikejdbc._
import java.util.UUID

object UserDao {
  implicit val session: AutoSession = AutoSession

  case class User(id: Option[String], email: String, password: String)
  case class StoredUser(id: String, email: String, password: String, salt: String)

  def addUser(id: UUID, email: String, password: String, salt: String): Unit = {
    sql"insert into user (ID, EMAIL, PASSWORD, SALT) values ($id, $email, $password, $salt)"
      .update()
      .apply()
  }

  def fetchUserByEmail(email: String): Option[StoredUser] =
    sql"select * from user where EMAIL = $email"
      .map(res => StoredUser(res.string("ID"), res.string("EMAIL"), res.string("PASSWORD"), res.string("SALT")))
      .single()
      .apply()
}