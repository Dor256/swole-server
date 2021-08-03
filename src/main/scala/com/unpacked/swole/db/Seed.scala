package com.unpacked.swole.db

import scalikejdbc._
import scala.io.Source

object Seed {
  implicit val session: AutoSession = AutoSession

  def apply(): Unit = {
    val schema = Source.fromResource("schema.sql")
    val statements = schema.getLines()
      .filterNot(line => line.startsWith("--") || line.startsWith("/*") || line.isEmpty)
      .mkString
      .split(";")
      .map(SQL(_).execute().apply())
  }
}