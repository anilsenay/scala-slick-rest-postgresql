package com.anilsenay.models

import spray.json.DefaultJsonProtocol

case class Address(id: Option[String], title: String, city: String, region: String, zipcode: String, fullAddress: String)

object Address extends DefaultJsonProtocol {
  implicit val addressFormat = jsonFormat6(Address.apply)
}
