package com.anilsenay.models

import spray.json.DefaultJsonProtocol

case class Brand(id: Option[Long], brandName: String)

object Brand extends DefaultJsonProtocol {
  implicit val brandFormat = jsonFormat2(Brand.apply)
}
