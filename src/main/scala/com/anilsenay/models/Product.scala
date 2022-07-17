package com.anilsenay.models

import com.anilsenay.utils.BaseJsonProtocol

import java.sql.Timestamp

case class Product(
                  id: Option[Long],
                  productName: String,
                  coverPhotoIndex: Int,
                  information: String,
                  price: Double,
                  salePrice: Double,
                  brandId: Option[Long],
                  categoryId: Option[Long],
                  createdAt: Timestamp
                )

object Product extends BaseJsonProtocol {
  implicit val productFormat = jsonFormat9(Product.apply)
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

object ProductPost extends BaseJsonProtocol {
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

object ProductUpdate extends BaseJsonProtocol {
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
                    sizes: Seq[String],
                    createdAt: Timestamp,
                      )

object FullProduct extends BaseJsonProtocol {
  implicit val productFormat = jsonFormat11(FullProduct.apply)
}