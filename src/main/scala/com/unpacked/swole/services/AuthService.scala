package com.xpacked.swole.services

import pdi.jwt.JwtClaim
import pdi.jwt.JwtAlgorithm
import pdi.jwt.JwtCirce
import java.time.Instant

object AuthService {
  val BEARER_PREFIX = "Bearer "

  def generateJWT(email: String): String = {
    val claim = JwtClaim(
      expiration = Some(Instant.now.plusSeconds(157784760).getEpochSecond),
      issuedAt = Some(Instant.now.getEpochSecond),
      issuer = Some(email)
    )
    val key = "secretKey"
    val algo = JwtAlgorithm.HS256
    JwtCirce.encode(claim, key, algo)
  }

  def validateJWT(jwt: String): Option[JwtClaim] = {
    val key = "secretKey"
    JwtCirce.decode(jwt, key, Seq(JwtAlgorithm.HS256)).toOption
  }

  def ensureBearerToken(token: String): Option[String] =
    if (token.contains(BEARER_PREFIX)) Some(token.stripPrefix(BEARER_PREFIX))
    else None
}