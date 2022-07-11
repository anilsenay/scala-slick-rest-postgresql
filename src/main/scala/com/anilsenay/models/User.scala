package com.anilsenay.models

import spray.json.DefaultJsonProtocol

case class User(id: Option[String], name: String, surname: String, email: String, phone: String)
case class UserWithAddress(user: User, address: Seq[Address])

object User extends DefaultJsonProtocol {
  implicit val userFormat = jsonFormat5(User.apply)
}

object UserWithAddress extends DefaultJsonProtocol {
  implicit val userFormat = jsonFormat2(UserWithAddress.apply)
}