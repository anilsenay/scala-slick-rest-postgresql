package com.anilsenay.schema

import com.anilsenay.models.ProductImage
import slick.jdbc.PostgresProfile.api._

object ProductImageTable {

  class ProductImages(tag: Tag) extends Table[ProductImage](tag, "product_photo") {
    def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
    def url = column[String]("url")
    def productId = column[Option[Long]]("product_id")

    def * =
      (id, url, productId) <> ((ProductImage.apply _).tupled, ProductImage.unapply)
  }

  val productImages = TableQuery[ProductImages]

}
