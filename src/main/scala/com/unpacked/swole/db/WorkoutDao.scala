package com.unpacked.swole.db

import scalikejdbc._
import java.util.UUID

object WorkoutDao {
  implicit val session: AutoSession = AutoSession

  case class Workout(id: Option[String], name: String, goal: String)

  def addWorkout(workout: Workout, email: String): Unit = {
    sql"insert into workout (ID, NAME, GOAL) values (${workout.id}, ${workout.name}, ${workout.goal})"
      .update()
      .apply()

    sql"insert into user_to_workout(USER_EMAIL, ID) values ($email, ${workout.id})"
      .update()
      .apply()
  }

  def fetchAllWorkouts(email: String): List[Workout] =
    sql"""
      select * from workout
      inner join user_to_workout
      on workout.ID = user_to_workout.ID
      where user_to_workout.USER_EMAIL = $email
    """
      .map(res => Workout(res.stringOpt("ID"), res.string("NAME"), res.string("GOAL")))
      .list()
      .apply()

  def fetchWorkoutById(workoutId: UUID, email: String): Option[Workout] =
    sql"""
      select * from workout
      inner join user_to_workout
      on workout.ID = user_to_workout.ID
      where user_to_workout.USER_EMAIL = $email and workout.ID = $workoutId
    """
      .map(res => Workout(res.stringOpt("ID"), res.string("NAME"), res.string("GOAL")))
      .single()
      .apply()

  def deleteWorkout(workoutId: UUID, email: String): Unit =
    sql"delete from workout where ID = $workoutId"
      .update()
      .apply()

  def updateWorkout(workoutId: UUID, workout: Workout, email: String): Unit =
    sql"""
      update workout
      set NAME = ${workout.name}, GOAL = ${workout.goal}
      where ID = (select ID from user_to_workout where USER_EMAIL = $email and ID = $workoutId)
    """
      .update()
      .apply()
}