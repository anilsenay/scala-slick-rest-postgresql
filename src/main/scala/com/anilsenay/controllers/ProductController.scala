package com.anilsenay.controllers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.anilsenay.models.{Product, ProductPost, ProductUpdate}
import com.anilsenay.services.ProductService
import com.typesafe.scalalogging.LazyLogging
import spray.json._

import scala.util.{Failure, Success}

class ProductController(dbService: ProductService) extends SprayJsonSupport with DefaultJsonProtocol with LazyLogging {
  val route: Route = pathPrefix("api" / "product") {
    get {
      parameters(
        "sort".as[String],
        "category".as[String],
        "min".as[Double].withDefault(-1),
        "max".as[Double].withDefault(Double.MaxValue),
        "brand".as[String]
      ) { (sort, category, min, max, brand) =>
        complete(dbService.getAllProductsWithFilter(category, sort, min, max, brand))
      } ~
      path(LongNumber) {
        (productId) => {
          val q = dbService.getProduct(productId)
          onComplete(q) {
            case Success(result) => {
              complete(result)
            }
            case Failure(e) => {
              println(e)
              complete(StatusCodes.InternalServerError)
            }
          }
        }
      } ~
      pathEndOrSingleSlash {
        complete(dbService.getAllProducts)
      }
    } ~ post {
      path(LongNumber / "photo") {
        productId => {
          entity(as[Seq[String]]) { photos =>
            val saved = dbService.insertProductImage(productId, photos)
            onComplete(saved) {
              case Success(savedProductImages) => {
                logger.info(s"Inserted photos to product with id:${productId}")
                complete(JsObject("InsertedPhotos" -> JsNumber(savedProductImages.flatten.length)))
              }
              case Failure(e) => {
                println(s"Failed to insert images", e)
                complete(StatusCodes.InternalServerError)
              }
            }
          }
        }
      } ~
      path(LongNumber / "size") {
        productId => {
          entity(as[Seq[String]]) { photos =>
            val saved = dbService.insertProductSizes(productId, photos)
            onComplete(saved) {
              case Success(savedProductSizes) => {
                logger.info(s"Inserted sizes to product with id:${productId}")
                complete(JsObject("InsertedSizes" -> JsNumber(savedProductSizes.flatten.length)))
              }
              case Failure(e) => {
                println(s"Failed to insert sizes", e)
                complete(StatusCodes.InternalServerError)
              }
            }
          }
        }
      } ~
      pathEndOrSingleSlash {
        entity(as[ProductPost]) { p: ProductPost =>
          val saved = dbService.insertProduct(p.productName, p.coverPhotoIndex, p.information, p.price, p.salePrice, p.brandId, p.categoryId, p.photos, p.sizes)
          onComplete(saved) {
            case Success(savedProduct) => {
              logger.info(s"Inserted product with id:${savedProduct}")
              complete(JsObject(
                "product" -> savedProduct._1.toJson,
                "photos" -> savedProduct._2.toJson,
                "sizes" -> savedProduct._3.toJson
              ))
            }
            case Failure(e) => {
              println(s"Failed to insert  product", e)
              complete(StatusCodes.InternalServerError)
            }
          }
        }
      }
    } ~
    put {
      path(LongNumber) { id =>
        entity(as[ProductUpdate]) { product =>
          val updated = dbService.update(id, product)
          onComplete(updated) {
            case Success(updatedRows) => complete(JsObject("updatedRows" -> JsNumber(updatedRows)))
            case Failure(e) => {
              logger.error(s"Failed to update a person ${id}", e)
              complete(StatusCodes.InternalServerError)
            }
          }
        }
      }
    } ~
    delete {
      path(LongNumber / "size" / Segment) {
        (productId, size) => {
          val saved = dbService.deleteSize(productId, size)
          onComplete(saved) {
            case Success(savedProductSizes) => {
              logger.info(s"Inserted sizes to product with id:${productId}")
              complete(JsObject("InsertedSizes" -> JsNumber(savedProductSizes)))
            }
            case Failure(e) => {
              println(s"Failed to insert sizes", e)
              complete(StatusCodes.InternalServerError)
            }
          }
        }
      } ~
      path(LongNumber / "photo") {
        (productId) => {
          entity(as[Seq[String]]) { photos =>
            val saved = dbService.deleteImage(productId, photos)
            println("assdassa")
            onComplete(saved) {
              case Success(savedProductSizes) => {
                logger.info(s"Inserted sizes to product with id:${productId}")
                complete(JsObject("InsertedSizes" -> JsNumber(savedProductSizes)))
              }
              case Failure(e) => {
                println(s"Failed to insert sizes", e)
                complete(StatusCodes.InternalServerError)
              }
            }
          }
        }
      } ~
      path(LongNumber) { id =>
        val deleted = dbService.delete(id)
        onComplete(deleted) {
          case Success(updatedRows) => complete(JsObject("deletedRows" -> JsNumber(updatedRows)))
          case Failure(e) => {
            logger.error(s"Failed to update a person ${id}", e)
            complete(StatusCodes.InternalServerError)
          }
        }
      }
    }
  }
}
