package com.anilsenay.services

import akka.http.scaladsl.model.HttpHeader.ParsingResult.Ok
import com.anilsenay.models.{User, UserWithAddress}
import com.anilsenay.schema.UsersTable._
import com.anilsenay.schema.AddressTable._
import com.anilsenay.schema.UserAddressTable._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class UserService(db: Database)(implicit ec: ExecutionContext) {
  def getAllPeople: Future[Seq[User]] = db.run(users.result)

  def getUser(id: Long): Future[Option[User]] = {
    db.run {
      users.filter(_.id === id).result.headOption
    }
  }

  def getUserWithAddresses(id: Long): Future[Option[UserWithAddress]] = {
    db.run {
      (for {
        user <- users.filter(_.id === id)
        userAddress <- userAddresses.filter(_.userId === id)
        address <- addresses.filter(_.id === userAddress.addressId)
      } yield (user, address)).result.map {
        _.groupBy(_._1)
          .map {
            case (u, seq) => UserWithAddress(u.id, u.name, u.surname, u.email, u.phone, seq.map(_._2))
          }.toSeq.headOption
      }
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

  def update(id: Long, user: User): Future[Int] = {
    val action = users
      .filter(_.id === id)
      .map(u => (u.name, u.surname, u.email, u.phone))
      .update((user.name, user.surname, user.email, user.phone))

    db.run(action)
  }

  def delete(id: Long): Future[Int] = {
    db.run {
      users.filter(_.id === id).delete
    }
  }
}