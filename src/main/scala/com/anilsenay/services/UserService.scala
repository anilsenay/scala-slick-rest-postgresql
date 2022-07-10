package com.anilsenay.services

import com.anilsenay.models.User
import com.anilsenay.schema.UsersTable._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class UserService(db: Database)(implicit ec: ExecutionContext) {
  def getAllPeople: Future[Seq[User]] = db.run(users.result)

  def getUser(id: String): Future[Option[User]] = {
    db.run {
      users.filter(_.id === id).result.headOption
    }
  }

  def insertUser(name: String, surname: String, email: String, phone: String): Future[User] = {
    val action = (users.map(user => (user.name, user.surname, user.email, user.phone)) returning users
      .map(_.id) into (
      (userData,
       id) => User(id, userData._1, userData._2, userData._3, userData._4)
      )) += (name, surname, email, phone)
    db.run(action)
  }

  def update(id: String, user: User): Future[Int] = {
    val action = users
      .filter(_.id === id)
      .map(u => (u.name, u.surname, u.email, u.phone))
      .update((user.name, user.surname, user.email, user.phone))

    db.run(action)
  }

  def delete(id: String): Future[Int] = {
    db.run {
      users.filter(_.id === id).delete
    }
  }
}