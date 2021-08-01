package com.unpacked.swole.routers

import cats.effect._
import cats.implicits._
import org.http4s.syntax.header._
import org.http4s.headers.Authorization
import org.http4s.dsl.Http4sDsl
import org.http4s.HttpRoutes
import com.unpacked.swole.db.UserDao._
import com.github.t3hnar.bcrypt._
import org.http4s.EntityDecoder
import io.circe.syntax._
import org.http4s.circe._
import io.circe.generic.auto._
import java.util.UUID
import org.http4s.EntityEncoder
import com.unpacked.swole.services.AuthService._

object AuthRouter {
  def apply[F[_]: Concurrent]: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    implicit val userDecoder: EntityDecoder[F, User] = jsonOf[F, User]

    HttpRoutes.of[F] {
      case req @ GET -> Root / "authorize" => {
        val maybeUser = for {
          token <- req.headers.get[Authorization]
          bearer <- ensureBearerToken(token.value)
          claim <- validateJWT(bearer)
          email <- claim.issuer
          user <- fetchUserByEmail(email)
        } yield user

        maybeUser match {
          case None => Forbidden("Bad Authorization Token")
          case Some(user) => {
            val partialUser = Map(
              "id" -> user.id,
              "email" -> user.email
            )
            Ok(partialUser.asJson)
          }
        }
      }
      case req @ POST -> Root / "signup" =>
        for {
          user <- req.as[User]
          salt = BCrypt.gensalt()
          password = (user.password + salt).bcrypt
          uuid = UUID.randomUUID()
          jwt = generateJWT(user.email)
          _ = addUser(uuid, user.email, password, salt, jwt)
          response = Map(
            "id" -> uuid.toString(),
            "email" -> user.email,
            "jwt" -> jwt
          )
          res <- Ok(response.asJson)
        } yield res
      case req @ POST -> Root / "login" => 
        for {
          user <- req.as[User]
          res <- fetchUserByEmail(user.email) match {
            case Some(storedUser) => {
              val isVerified = (user.password + storedUser.salt).isBcrypted(storedUser.password)
              val response = Map(
                "id" -> storedUser.id,
                "email" -> storedUser.email,
                "jwt" -> generateJWT(storedUser.email)
              )
              if (isVerified) Ok(response.asJson) else Forbidden("Wrong Email or Password")
            }
            case None => NotFound("Wrong Email or Password")
          }
        } yield res
    }
  }
}
