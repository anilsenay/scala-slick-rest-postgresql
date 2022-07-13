package com.anilsenay.services

import com.anilsenay.schema.ProductTable._
import com.anilsenay.models.{FullProduct, Product}
import com.anilsenay.schema.BrandTable.brands
import com.anilsenay.schema.CategoryTable.categories
import com.anilsenay.schema.ProductImageTable.productImages
import com.anilsenay.schema.ProductSizeTable.{ProductSizes, productSizes}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class ProductService(db: Database)(implicit ec: ExecutionContext) {
  def getAllProducts: Future[Seq[FullProduct]] = {
    /*db.run {
      (for {
        product <- products
        brand <- brands.filter(_.id === product.brandId)
        category <- categories.filter(_.id === product.categoryId)
        photo <- productImages.filter(_.productId === product.id)
        sizes <- productSizes.filter(_.productId === product.id)
      } yield (product, brand, category, photo, sizes)).result.map {
        _.groupBy(_._1)
          .map {
            case (p, seq) => FullProduct(p.id, p.productName, p.coverPhotoIndex, p.information, p.price, p.salePrice, seq.map(_._2).headOption, seq.map(_._3).headOption, seq.map(i => Some(i._4.url)).distinct, seq.map(i => Some(i._5)).distinct)
          }.toSeq
      }
    }*/
    db.run {
      val productQuery = for {
        product <- products
        brand <- brands.filter(_.id === product.brandId)
        category <- categories.filter(_.id === product.categoryId)
      } yield (product, brand, category)

      val withPhotos = for {
        (person, photos) <- productQuery.joinLeft(productImages).on(_._1.id === _.productId)
      } yield (person, photos)

      val withSizes = for {
        (person, sizes) <- withPhotos.joinLeft(productSizes).on(_._1._1.id === _.productId)
      } yield (person, sizes)

      withSizes.result
    }.map {
      tuples =>
        tuples.groupBy(_._1._1._1).map{
          case (p, tuples) => {
            val brand = tuples.map(_._1._1._2).headOption
            val category = tuples.map(_._1._1._3).headOption
            val photos = tuples.map(_._1._2.map(_.url)).distinct.flatten
            val sizes = tuples.map(_._2).distinct.flatten

            FullProduct(p.id, p.productName, p.coverPhotoIndex, p.information, p.price, p.salePrice, brand, category, photos, sizes)
          }
        }.toSeq
    }
  }

  def getProduct(id: Long) = {
    db.run {
      val productQuery = for {
        product <- products.filter(_.id === id)
        brand <- brands.filter(_.id === product.brandId)
        category <- categories.filter(_.id === product.categoryId)
      } yield (product, brand, category)

      val withPhotos = for {
        (person, photos) <- productQuery.joinLeft(productImages).on(_._1.id === _.productId)
      } yield (person, photos)

      val withSizes = for {
        (person, sizes) <- withPhotos.joinLeft(productSizes).on(_._1._1.id === _.productId)
      } yield (person, sizes)

      withSizes.result
    }.map {
      tuples =>
        tuples.groupBy(_._1._1._1).map{
          case (p, tuples) => {
            val brand = tuples.map(_._1._1._2).headOption
            val category = tuples.map(_._1._1._3).headOption
            val photos = tuples.map(_._1._2.map(_.url)).distinct.flatten
            val sizes = tuples.map(_._2).distinct.flatten

            FullProduct(p.id, p.productName, p.coverPhotoIndex, p.information, p.price, p.salePrice, brand, category, photos, sizes)
          }
        }.toSeq.headOption
    }
  }

}