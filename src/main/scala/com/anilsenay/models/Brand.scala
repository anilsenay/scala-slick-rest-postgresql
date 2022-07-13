package com.anilsenay.models

import spray.json.DefaultJsonProtocol

case class Brand(id: Option[String], brandName: String)

object Brand extends DefaultJsonProtocol {
  implicit val brandFormat = jsonFormat2(Brand.apply)
}
