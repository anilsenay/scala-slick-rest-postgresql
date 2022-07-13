package com.anilsenay.schema

import com.anilsenay.models.ProductSize
import slick.jdbc.PostgresProfile.api._

object ProductSizeTable {

  class ProductSizes(tag: Tag) extends Table[ProductSize](tag, "product_size") {
    def id = column[Option[String]]("id", O.PrimaryKey)
    def size = column[String]("size")
    def productId = column[Option[String]]("product_id")

    def * =
      (id, size, productId) <> ((ProductSize.apply _).tupled, ProductSize.unapply)
  }

  val productSizes = TableQuery[ProductSizes]

}
