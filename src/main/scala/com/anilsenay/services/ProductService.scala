package com.anilsenay.services

import com.anilsenay.schema.ProductTable._
import com.anilsenay.models.{FullProduct, Product, ProductImage, ProductSize}
import com.anilsenay.schema.BrandTable.brands
import com.anilsenay.schema.CategoryTable.categories
import com.anilsenay.schema.ProductImageTable.productImages
import com.anilsenay.schema.ProductSizeTable.{ProductSizes, productSizes}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class ProductService(db: Database)(implicit ec: ExecutionContext) {
  def getAllProducts: Future[Seq[FullProduct]] = {
    println("all")

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
      val productQuery = (for {
        product <- products
        brand <- brands.filter(_.id === product.brandId)
        category <- categories.filter(_.id === product.categoryId)
      } yield (product, brand, category))

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
            val sizes = tuples.map(_._2.map(_.size)).distinct.flatten

            FullProduct(p.id, p.productName, p.coverPhotoIndex, p.information, p.price, p.salePrice, brand, category, photos, sizes)
          }
        }.toSeq
    }
  }

  def getAllProductsWithFilter(cat: String, sort: String, min: Double = -1, max: Double = Double.MaxValue): Future[Seq[FullProduct]] = {
    println("filter")
    db.run {
      val productQuery = (for {
        product <- products.filter(_.salePrice >= min).filter(_.salePrice <= max)
        brand <- brands.filter(_.id === product.brandId)
        category <- {
          cat match {
            case cat if cat.nonEmpty => categories.filter(_.id === product.categoryId).filter(_.categoryName === cat)
            case _ => categories.filter(_.id === product.categoryId)
          }
        }
      } yield (product, brand, category))

      val withPhotos = for {
        (person, photos) <- productQuery.joinLeft(productImages).on(_._1.id === _.productId)
      } yield (person, photos)

      val withSizes = for {
        (person, sizes) <- withPhotos.joinLeft(productSizes).on(_._1._1.id === _.productId)
      } yield (person, sizes)

      withSizes.result
    }.map {
      tuples =>
        val sequence = tuples.groupBy(_._1._1._1).map{
          case (p, tuples) => {
            val brand = tuples.map(_._1._1._2).headOption
            val category = tuples.map(_._1._1._3).headOption
            val photos = tuples.map(_._1._2.map(_.url)).distinct.flatten
            val sizes = tuples.map(_._2.map(_.size)).distinct.flatten

            FullProduct(p.id, p.productName, p.coverPhotoIndex, p.information, p.price, p.salePrice, brand, category, photos, sizes)
          }
        }.toSeq

        sort match {
          case "asc" => sequence.sortBy(_.salePrice)
          case "desc" => sequence.sortBy(_.salePrice)(Ordering[Double].reverse)
          case "a-z" => sequence.sortBy(_.productName.toLowerCase)
          case "z-a" => sequence.sortBy(_.productName.toLowerCase)(Ordering[String].reverse)
          case _ => sequence.sortBy(_.salePrice)
        }
    }
  }

  def getProduct(id: Long): Future[Option[FullProduct]] = {
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
        tuples.groupBy(_._1._1._1).map {
          case (p, tuples) => {
            val brand = tuples.map(_._1._1._2).headOption
            val category = tuples.map(_._1._1._3).headOption
            val photos = tuples.map(_._1._2.map(_.url)).distinct.flatten
            val sizes = tuples.map(_._2.map(_.size)).distinct.flatten

            FullProduct(p.id, p.productName, p.coverPhotoIndex, p.information, p.price, p.salePrice, brand, category, photos, sizes)
          }
        }.toSeq.headOption
    }
  }

  def insertProduct(productName: String, coverPhotoIndex: Int, information: String, price: Double, salePrice: Double, brandId: Option[Long], categoryId: Option[Long], photos: Seq[String] = Seq(), sizes: Seq[String] = Seq()) = {
    val action = (products.map(product => (product.productName, product.coverPhotoIndex, product.information, product.price, product.salePrice, product.brandId, product.categoryId)) returning products
      .map(_.id) into (
      (productData, id) => Product(id, productData._1, productData._2, productData._3, productData._4, productData._5, productData._6, productData._7)
      )) += (productName, coverPhotoIndex, information, price, salePrice, brandId, categoryId)

    (for {
      product <- db.run(action)
      photos <- db.run((productImages returning productImages.map(_.id)) ++= photos.map(i => ProductImage(None, i, product.id)))
      sizes <- db.run((productSizes returning productSizes.map(_.id)) ++= sizes.map(i => ProductSize(None, i, product.id)))
    } yield (product, photos, sizes))
  }

  def insertProductImage(productId: Long, photos: Seq[String] = Seq()) = {
    db.run((productImages returning productImages.map(_.id)) ++= photos.map(i => ProductImage(None, i, Some(productId))))
  }

  def insertProductSizes(productId: Long, sizes: Seq[String] = Seq()) = {
    db.run((productSizes returning productSizes.map(_.id)) ++= sizes.map(i => ProductSize(None, i, Some(productId))))
  }

  def delete(id: Long): Future[Int] = {
    db.run {
      products.filter(_.id === id).delete
      productImages.filter(_.productId === id).delete
      productSizes.filter(_.productId === id).delete
    }
  }

  def deleteSize(productId: Long, size: String): Future[Int] = {
    db.run {
      productSizes.filter(_.productId === productId).filter(_.size === size).delete
    }
  }

  def deleteImage(productId: Long, photos: Seq[String] = Seq()) = {
    // TODO: This function deletes all products for now
    db.run {
      productImages.filter(_.productId === productId).delete
    }
  }

}