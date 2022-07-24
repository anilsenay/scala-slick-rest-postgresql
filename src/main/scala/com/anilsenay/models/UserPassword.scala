package com.anilsenay.models

import spray.json.DefaultJsonProtocol

case class UserPassword(user_id: Option[Long], password: String, salt: String)

object UserPassword extends DefaultJsonProtocol {
  implicit val userPasswordFormat = jsonFormat2(UserAddress.apply)
}