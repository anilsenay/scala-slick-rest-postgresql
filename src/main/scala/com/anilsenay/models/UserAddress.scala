package com.anilsenay.models

import spray.json.DefaultJsonProtocol

case class UserAddress(user_id: Option[Long], address_id: Option[Long])

object UserAddress extends DefaultJsonProtocol {
  implicit val userAddressFormat = jsonFormat2(UserAddress.apply)
}