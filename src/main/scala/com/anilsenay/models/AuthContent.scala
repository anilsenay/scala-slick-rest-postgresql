package com.anilsenay.models

import spray.json.DefaultJsonProtocol

case class AuthContent(email: String, password: String)

object AuthContent extends DefaultJsonProtocol {
  implicit val authFormat = jsonFormat2(AuthContent.apply)
}

case class AuthResponse(id: Long, email: String, token: String)

object AuthResponse extends DefaultJsonProtocol {
  implicit val authResponseFormat = jsonFormat3(AuthResponse.apply)
}
