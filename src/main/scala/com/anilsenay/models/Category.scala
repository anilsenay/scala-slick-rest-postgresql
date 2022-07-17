package com.anilsenay.models

import spray.json.DefaultJsonProtocol

case class Category(id: Option[Long], categoryName: String)

object Category extends DefaultJsonProtocol {
  implicit val categoryFormat = jsonFormat2(Category.apply)
}
