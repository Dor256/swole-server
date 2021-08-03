package com.unpacked.swole.db

import scalikejdbc._
import scala.io.Source

object Seed {
  implicit val session: AutoSession = AutoSession

  def dangerousSql(statement: String): Boolean =
    statement.startsWith("--") ||
    statement.startsWith("/*") ||
    statement.isEmpty

  def apply(): Unit = {
    val schema = Source.fromResource("schema.sql")
    val statements = schema.getLines()
      .filterNot(dangerousSql)
      .mkString
      .split(";")
      .map(SQL(_).execute().apply())
  }
}