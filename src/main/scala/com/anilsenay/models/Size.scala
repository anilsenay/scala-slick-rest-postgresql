package com.anilsenay.models

import spray.json.DefaultJsonProtocol

case class Size(id: Option[Long], size: String)

object Size extends DefaultJsonProtocol {
  implicit val sizeFormat = jsonFormat2(Size.apply)
}
