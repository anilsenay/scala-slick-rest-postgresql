package com.anilsenay.schema

import com.anilsenay.models.Product
import com.anilsenay.schema.BrandTable.Brands
import com.anilsenay.schema.CategoryTable.Categories
import slick.jdbc.PostgresProfile.api._

object ProductTable {

  class Products(tag: Tag) extends Table[Product](tag, "product") {
    def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
    def productName = column[String]("product_name")
    def brandId = column[Option[Long]]("brand_id")
    def categoryId = column[Option[Long]]("category_id")
    def coverPhotoIndex = column[Int]("cover_photo_index")
    def information = column[String]("information")
    def price = column[Double]("price")
    def salePrice = column[Double]("sale_price")

    def * =
      (id, productName, brandId, categoryId, coverPhotoIndex, information, price, salePrice) <> ((Product.apply _).tupled, Product.unapply)

    def brand = foreignKey("fk_brand", brandId, TableQuery[Brands])(_.id)
    def category = foreignKey("fk_category", categoryId, TableQuery[Categories])(_.id)

  }

  val products = TableQuery[Products]

}
