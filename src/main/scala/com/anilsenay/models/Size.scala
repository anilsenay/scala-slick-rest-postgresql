package com.anilsenay.models

import spray.json.DefaultJsonProtocol

case class Size(id: Option[String], size: String)

object Size extends DefaultJsonProtocol {
  implicit val sizeFormat = jsonFormat2(Size.apply)
}
