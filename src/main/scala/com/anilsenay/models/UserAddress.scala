package com.anilsenay.models

import spray.json.DefaultJsonProtocol

import java.util.UUID

case class UserAddress(address_id: Option[String], user_id: Option[String])
case class UserAddressPost(address_id: Option[String], user_id: Option[UUID])

object UserAddress extends DefaultJsonProtocol {
  implicit val userAddressFormat = jsonFormat2(UserAddress.apply)
}