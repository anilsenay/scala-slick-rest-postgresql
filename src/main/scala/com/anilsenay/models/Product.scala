package com.anilsenay.models

import spray.json.DefaultJsonProtocol

case class Product(
                  id: Option[Long],
                  productName: String,
                  brandId: Option[Long],
                  categoryId: Option[Long],
                  coverPhotoIndex: Int,
                  information: String,
                  price: Double,
                  salePrice: Double,
                )

object Product extends DefaultJsonProtocol {
  implicit val productFormat = jsonFormat8(Product.apply)
}

case class FullProduct(
                    id: Option[Long],
                    productName: String,
                    coverPhotoIndex: Int,
                    information: String,
                    price: Double,
                    salePrice: Double,
                    brand: Option[Brand],
                    category: Option[Category],
                    photos: Seq[String],
                    sizes: Seq[ProductSize]
                  )

object FullProduct extends DefaultJsonProtocol {
  implicit val productFormat = jsonFormat10(FullProduct.apply)
}