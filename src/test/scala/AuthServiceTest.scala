import org.scalatest.funspec.AnyFunSpec
import com.xpacked.swole.services.AuthService._

class AuthServiceTest extends AnyFunSpec {
  describe("JWT token") {
    it("Returns Some for a token with the 'Bearer' prefix") {
      val mockedValidJWT = "Bearer someRandomJWT"
      assert(ensureBearerToken(mockedValidJWT) == Some("someRandomJWT"))
    }

    it("Returns None for a token without the 'Bearer' prefix") {
      val mockedInvalidJWT = "invalidJWT"
      assert(ensureBearerToken(mockedInvalidJWT) == None)
    }
  }
}
