package com.anilsenay.services

import com.anilsenay.models.{User, UserWithAddress}
import com.anilsenay.schema.UsersTable._
import com.anilsenay.schema.AddressTable._
import com.anilsenay.schema.UserAddressTable._
import com.anilsenay.utils.SelectedProfile.api._

import scala.concurrent.Future

object UserService extends BaseService {
  def getAllPeople: Future[Seq[User]] = db.run(users.result)

  def getUser(id: Long): Future[Option[User]] = {
    db.run {
      users.filter(_.id === id).result.headOption
    }
  }

  def getUserWithAddresses(id: Long): Future[(Option[User], Option[UserWithAddress])] = {
    for {
      user <- db.run(users.filter(_.id === id).result.headOption)
      address <- db.run {
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
    } yield (user, address)
  }

  def insertUser(
                  name: String,
                  surname: String,
                  email: String,
                  phone: Option[String],
                  isAdmin: Option[Boolean] = Some(false)
                ): Future[User] = {
    val action = (users.map(user => (user.name, user.surname, user.email, user.phone, user.isAdmin)) returning users
      .map(_.id) into (
      (userData,
       id) => User(id, userData._1, userData._2, userData._3, userData._4, userData._5)
      )) += (name, surname, email, phone, isAdmin)
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