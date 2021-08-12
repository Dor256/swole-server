import org.scalatest.funspec.AnyFunSpec
import com.xpacked.swole.db.UserDao._
import scalikejdbc.ConnectionPool
import org.scalatest.BeforeAndAfterEach
import java.util.UUID
import com.xpacked.swole.db.Seed
import org.scalatest.BeforeAndAfterAll
import scalikejdbc._

class UserDaoTest extends AnyFunSpec with BeforeAndAfterEach with BeforeAndAfterAll {
  override def beforeAll(): Unit =
    Class.forName("org.h2.Driver")
    ConnectionPool.singleton("jdbc:h2:mem:test;MODE=PostgreSQL", "sa", "")

  override def beforeEach(): Unit =
    Seed()

  override def afterEach(): Unit =
    sql"drop table if exists user".execute().apply()

  describe("User Table") {
    it("Correctly adds and fetches a user") {
      val uuid = UUID.randomUUID()
      val email = "mock@email.com"
      val password = "mockPassword"
      val salt = "mockSalt"

      addUser(uuid, email, password, salt)
      val fetchedUser = fetchUserByEmail(email)

      assert(fetchedUser == Some(new StoredUser(uuid.toString, email, password, salt)))
    }
  }
}