package com.anilsenay.models

import spray.json.DefaultJsonProtocol

case class UserAddress(address_id: Option[String], user_id: Option[String])

object UserAddress extends DefaultJsonProtocol {
  implicit val userAddressFormat = jsonFormat2(UserAddress.apply)
}