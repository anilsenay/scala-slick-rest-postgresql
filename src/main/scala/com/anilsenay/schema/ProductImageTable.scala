package com.anilsenay.schema

import com.anilsenay.models.ProductImage
import com.anilsenay.schema.ProductTable.Products
import com.anilsenay.utils.SelectedProfile.api._

object ProductImageTable {

  class ProductImages(tag: Tag) extends Table[ProductImage](tag, "product_photo") {
    def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
    def url = column[String]("url")
    def productId = column[Option[Long]]("product_id")

    def * =
      (id, url, productId) <> ((ProductImage.apply _).tupled, ProductImage.unapply)

    def product = foreignKey("fk_product", productId, TableQuery[Products])(_.id, onDelete=ForeignKeyAction.Cascade)

  }

  val productImages = TableQuery[ProductImages]

}
