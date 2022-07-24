package com.anilsenay.models

import spray.json.DefaultJsonProtocol

case class User(id: Option[Long], name: String, surname: String, email: String, phone: Option[String], isAdmin: Option[Boolean] = Some(false))
case class UserWithAddress(id: Option[Long], name: String, surname: String, email: String, phone: Option[String], address: Seq[Address])
case class UserRegister(id: Option[Long], name: String, surname: String, email: String, phone: Option[String], password: String)


object User extends DefaultJsonProtocol {
  implicit val userFormat = jsonFormat6(User.apply)
}

object UserWithAddress extends DefaultJsonProtocol {
  implicit val userWithAdressFormat = jsonFormat6(UserWithAddress.apply)
}

object UserRegister extends DefaultJsonProtocol {
  implicit val userRegisterFormat = jsonFormat6(UserRegister.apply)
}