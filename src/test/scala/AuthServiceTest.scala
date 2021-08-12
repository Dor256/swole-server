import org.scalatest.funspec.AnyFunSpec
import com.xpacked.swole.services.AuthService._
import pdi.jwt.JwtClaim

class AuthServiceTest extends AnyFunSpec {
  describe("JWT") {
    it("Returns Some for a token with the 'Bearer' prefix") {
      val mockedValidJWT = "Bearer someRandomJWT"
      assert(ensureBearerToken(mockedValidJWT) == Some("someRandomJWT"))
    }

    it("Returns None for a token without the 'Bearer' prefix") {
      val mockedInvalidJWT = "invalidJWT"
      assert(ensureBearerToken(mockedInvalidJWT) == None)
    }

    it("Returns correct issuer for JWT claim") {
      val issuer = "mock@email.com"
      val token = generateJWT(issuer)

      val claim = validateJWT(token)

      assert(claim.flatMap(_.issuer) == Some(issuer))
    }

    it("Returns None for wrong issuer of JWT claim") {
      val mockWrongToken = "wrongToken"
      val issuer = "mock@email.com"

      generateJWT(issuer)
      val claim = validateJWT(mockWrongToken)

      assert(claim.flatMap(_.issuer) == None)
    }
  }
}
