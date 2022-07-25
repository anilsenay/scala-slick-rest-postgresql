package com.anilsenay.models

import spray.json.DefaultJsonProtocol

case class JWTContent(id: Long, email: String, name: String, surname: String, isAdmin: Boolean)

case class JWTTokenOnly(token: String)

object JWTContent extends DefaultJsonProtocol {
  implicit val JWTContentFormat = jsonFormat5(JWTContent.apply)
}

object JWTTokenOnly extends DefaultJsonProtocol {
  implicit val JWTTokenOnlyFormat = jsonFormat1(JWTTokenOnly.apply)
}