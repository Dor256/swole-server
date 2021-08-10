package com.xpacked.swole.routers

import org.http4s._
import cats.effect._
import cats.implicits._
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._
import io.circe.generic.auto._
import io.circe.syntax._

import com.xpacked.swole.db.WorkoutDao._
import com.xpacked.swole.services.AuthService._
import java.util.UUID
import io.circe.Json


object WorkoutRouter {
  def apply[F[_]: Concurrent](email: String): HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    implicit val workoutDecoder: EntityDecoder[F, Workout] = jsonOf[F, Workout]

    HttpRoutes.of[F] {
      case req @ GET -> Root => Ok(fetchAllWorkouts(email).asJson) 
      case GET -> Root / UUIDVar(workoutId) =>
        fetchWorkoutById(workoutId, email) match {
          case Some(workout) => Ok(workout.asJson)
          case None => NotFound(s"Could not find workout with id $workoutId in the database")
        }
      case req @ POST -> Root =>
        for {
          workout <- req.as[Workout]
          uuid = UUID.randomUUID().toString
          _ = addWorkout(new Workout(Some(uuid), workout.name, workout.goal), email)
          res <- Ok()
        } yield res
      case req @ DELETE -> Root / UUIDVar(workoutId) => Ok(deleteWorkout(workoutId, email))
      case req @ PUT -> Root / UUIDVar(workoutId) =>
        for {
          workout <- req.as[Workout]
          _ = updateWorkout(workoutId, workout, email)
          res <- fetchWorkoutById(workoutId, email) match {
            case Some(workout) => Ok(workout.asJson)
            case None => NotFound(s"Could not find workout with id $workoutId in the database")
          }
        } yield res
    }
  }
}
