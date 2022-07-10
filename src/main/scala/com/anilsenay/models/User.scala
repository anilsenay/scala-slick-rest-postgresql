package com.anilsenay.models

import spray.json.DefaultJsonProtocol

case class User(id: Option[String], name: String, surname: String, email: String, phone: String)

object User extends DefaultJsonProtocol {
  implicit val userFormat = jsonFormat5(User.apply)
}