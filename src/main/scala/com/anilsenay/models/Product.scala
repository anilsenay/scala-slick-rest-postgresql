package com.anilsenay.models

import spray.json.DefaultJsonProtocol

case class Product(
                  id: Option[Long],
                  productName: String,
                  coverPhotoIndex: Int,
                  information: String,
                  price: Double,
                  salePrice: Double,
                  brandId: Option[Long],
                  categoryId: Option[Long]
                )

object Product extends DefaultJsonProtocol {
  implicit val productFormat = jsonFormat8(Product.apply)
}

case class ProductPost(
                    id: Option[Long],
                    productName: String,
                    coverPhotoIndex: Int,
                    information: String,
                    price: Double,
                    salePrice: Double,
                    brandId: Option[Long],
                    categoryId: Option[Long],
                    photos: Seq[String],
                    sizes: Seq[String]
                  )

object ProductPost extends DefaultJsonProtocol {
  implicit val productFormat = jsonFormat10(ProductPost.apply)
}

case class ProductUpdate(
                        productName: Option[String],
                        coverPhotoIndex: Option[Int],
                        information: Option[String],
                        price: Option[Double],
                        salePrice: Option[Double],
                        brandId: Option[Option[Long]],
                        categoryId: Option[Option[Long]],
                        photos: Option[Seq[String]],
                        sizes: Option[Seq[String]]
                      )

object ProductUpdate extends DefaultJsonProtocol {
  implicit val productFormat = jsonFormat9(ProductUpdate.apply)
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
                    sizes: Seq[String]
                  )

object FullProduct extends DefaultJsonProtocol {
  implicit val productFormat = jsonFormat10(FullProduct.apply)
}