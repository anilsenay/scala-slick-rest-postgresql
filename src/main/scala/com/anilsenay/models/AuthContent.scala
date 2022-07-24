package com.anilsenay.models

import spray.json.DefaultJsonProtocol

case class AuthContent(email: String, password: String)

object AuthContent extends DefaultJsonProtocol {
  implicit val authFormat = jsonFormat2(AuthContent.apply)
}

case class AuthResponse(id: Long, email: String, token: String, isAdmin: Boolean = false)

object AuthResponse extends DefaultJsonProtocol {
  implicit val authResponseFormat = jsonFormat4(AuthResponse.apply)
}
