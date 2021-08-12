import org.scalatest.funspec.AnyFunSpec
import org.scalatest.BeforeAndAfterEach
import scalikejdbc.ConnectionPool
import com.xpacked.swole.db.Seed
import com.xpacked.swole.db.WorkoutDao._
import java.util.UUID
import scalikejdbc._
import org.scalatest.BeforeAndAfterAll

class WorkoutDaoTest extends AnyFunSpec with BeforeAndAfterEach with BeforeAndAfterAll {
  override def beforeAll(): Unit =
    Class.forName("org.h2.Driver")
    ConnectionPool.singleton("jdbc:h2:mem:test;MODE=PostgreSQL", "sa", "")

  override def beforeEach(): Unit =
    Seed()

  override def afterEach(): Unit =
    sql"drop table if exists user_to_workout, workout".execute().apply()

  describe("Workout Table") {
    it("Adds and fetches a workout for the correct user") {
      val mockUserEmail = "mock@email.com"
      val uuid = UUID.randomUUID()
      val mockWorkout = new Workout(Some(uuid.toString), "mockName", "mockGoal")

      addWorkout(mockWorkout, mockUserEmail)
      val fetchedWorkout = fetchWorkoutById(uuid, mockUserEmail)

      assert(fetchedWorkout == Some(mockWorkout))
    }

    it("Returns all added workouts") {
      val mockEmail = "mock@email.com"
      val firstWorkout = new Workout(Some(UUID.randomUUID().toString), "firstName", "firstGoal")
      val secondWorkout = new Workout(Some(UUID.randomUUID().toString), "secondName", "secondGoal")
      addWorkout(firstWorkout, mockEmail)
      addWorkout(secondWorkout, mockEmail)

      val workoutList = fetchAllWorkouts(mockEmail)

      assert(workoutList == List(firstWorkout, secondWorkout))
    }

    it("Returns different workouts for different users") {
      val firstEmail = "mock1@email.com"
      val secondEmail = "mock2@email.com"
      val firstUUID = UUID.randomUUID()
      val secondUUID = UUID.randomUUID()
      val firstWorkout = new Workout(Some(firstUUID.toString), "firstWorkout", "firstGoal")
      val secondWorkout = new Workout(Some(secondUUID.toString), "secondWorkout", "secondGoal")
      addWorkout(firstWorkout, firstEmail)
      addWorkout(secondWorkout, secondEmail)

      val workoutsForFirstUser = fetchAllWorkouts(firstEmail)
      val workoutsForSecondUser = fetchAllWorkouts(secondEmail)

      assert(!workoutsForFirstUser.contains(secondWorkout))
      assert(!workoutsForSecondUser.contains(firstWorkout))
      assert(workoutsForFirstUser == List(firstWorkout))
      assert(workoutsForSecondUser == List(secondWorkout))
    }

    it("Updates a workout") {
      val mockEmail = "mock@email.com"
      val uuid = UUID.randomUUID()
      val mockWorkout = new Workout(Some(uuid.toString), "mockName", "mockGoal")
      addWorkout(mockWorkout, mockEmail)
      val workoutToUpdate = new Workout(Some(uuid.toString), "newName", "newGoal")

      updateWorkout(uuid, workoutToUpdate, mockEmail)
      val updatedWorkout = fetchWorkoutById(uuid, mockEmail)
      
      assert(updatedWorkout != Some(mockWorkout))
      assert(updatedWorkout == Some(workoutToUpdate))
    }

    it("Deletes a workout") {
      val mockEmail = "mock@email.com"
      val uuid = UUID.randomUUID()
      val mockWorkout = new Workout(Some(uuid.toString), "mockName", "mockGoal")
      addWorkout(mockWorkout, mockEmail)

      deleteWorkout(uuid, mockEmail)
      val deletedWorkout = fetchWorkoutById(uuid, mockEmail)

      assert(deletedWorkout == None)
    }
  }
}