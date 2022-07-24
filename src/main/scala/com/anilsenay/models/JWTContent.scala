package com.anilsenay.models

import spray.json.DefaultJsonProtocol

case class JWTContent(id: Long, email: String, name: String, surname: String)

case class JWTTokenOnly(token: String)

object JWTContent extends DefaultJsonProtocol {
  implicit val JWTContentFormat = jsonFormat4(JWTContent.apply)
}

object JWTTokenOnly extends DefaultJsonProtocol {
  implicit val JWTTokenOnlyFormat = jsonFormat1(JWTTokenOnly.apply)
}