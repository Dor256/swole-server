package com.unpacked.swole.middleware

import cats.effect._
import org.http4s.implicits._
import cats.data.Kleisli
import org.http4s.HttpRoutes
import com.unpacked.swole.services.AuthService._
import org.http4s.dsl.Http4sDsl
import org.http4s._
import cats.implicits._
import cats.data.OptionT
import org.http4s.headers.Authorization

object AuthMiddleware {
  def apply[F[_]: Concurrent](service: (String) => HttpRoutes[F]): HttpRoutes[F] = Kleisli { req =>
    val dsl = Http4sDsl[F]
    import dsl._

    val maybeEmail = maybeGetEmailFromJWT(req)
    maybeEmail match {
      case None => OptionT.liftF(Forbidden("Invalid Authorization Header!"))
      case Some(email) => service(email)(req)
    }
  }

  def maybeGetEmailFromJWT[F[_]: Concurrent](req: Request[F]): Option[String] =
    for {
      token <- req.headers.get[Authorization]
      bearer <- ensureBearerToken(token.value)
      claim <- validateJWT(bearer)
      email <- claim.issuer
    } yield email
}