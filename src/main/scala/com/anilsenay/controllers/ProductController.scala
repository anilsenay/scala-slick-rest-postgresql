package com.anilsenay.controllers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.anilsenay.models.{ProductPost, ProductUpdate}
import com.anilsenay.services.ProductService
import spray.json._

import scala.util.{Failure, Success}

class ProductController(dbService: ProductService.type) extends BaseController {
  val route: Route = pathPrefix("api" / "product") {
    get {
      path(LongNumber) {
        (productId) => {
          val q = dbService.getProduct(productId)
          onComplete(q) {
            case Success(result) => {
              complete(result)
            }
            case Failure(e) => {
              logger.error(s"Failed to fetch product $productId", e)
              complete(StatusCodes.InternalServerError)
            }
          }
        }
      } ~ parameters(
        "sort".as[String].optional,
        "category".as[String].optional,
        "min".as[Double].withDefault(-1),
        "max".as[Double].withDefault(Double.MaxValue),
        "brand".as[String].optional,
        "page".as[Int].optional
      ) { (sort, category, min, max, brand, page) =>
        complete(dbService.getAllProductsWithFilter(category, sort, min, max, brand, page))
      } ~ pathEndOrSingleSlash {
        complete(dbService.getAllProducts)
      }
    } ~ post {
      authenticate { authUser =>
        if (authUser.isAdmin) {
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
                    logger.error(s"Failed to insert product", e)
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
                    logger.error(s"Failed to insert product", e)
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
                  logger.error(s"Failed to insert product", e)
                  complete(StatusCodes.InternalServerError)
                }
              }
            }
          }
        }
        else complete(StatusCodes.Unauthorized)
      }
    } ~
    put {
      authenticate { authUser =>
        if (authUser.isAdmin) {
          path(LongNumber) { id =>
            entity(as[ProductUpdate]) { product =>
              println(authUser.email)
              val updated = dbService.update(id, product)
              onComplete(updated) {
                case Success(updatedRows) => complete(JsObject("updatedRows" -> JsNumber(updatedRows)))
                case Failure(e) => {
                  logger.error(s"Failed to update product ${id}", e)
                  complete(StatusCodes.InternalServerError)
                }
              }
            }
          }
        }
        else complete(StatusCodes.Unauthorized)
      }
    } ~
    delete {
      authenticate { authUser =>
        if (authUser.isAdmin) {
          path(LongNumber / "size" / Segment) {
            (productId, size) => {
              val saved = dbService.deleteSize(productId, size)
              onComplete(saved) {
                case Success(savedProductSizes) => {
                  logger.info(s"Deleted sizes to product with id:${productId}")
                  complete(JsObject("DeletedSizes" -> JsNumber(savedProductSizes)))
                }
                case Failure(e) => {
                  logger.error(s"Failed to delete sizes ${productId}", e)
                  complete(StatusCodes.InternalServerError)
                }
              }
            }
          } ~
          path(LongNumber / "photo") {
            (productId) => {
              entity(as[Seq[String]]) { photos =>
                val saved = dbService.deleteImage(productId, photos)
                onComplete(saved) {
                  case Success(savedProductSizes) => {
                    logger.info(s"Deleted sizes to product with id:${productId}")
                    complete(JsObject("DeletedSizes" -> JsNumber(savedProductSizes)))
                  }
                  case Failure(e) => {
                    logger.error(s"Failed to delete images ${productId}", e)
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
                logger.error(s"Failed to delete ${id}", e)
                complete(StatusCodes.InternalServerError)
              }
            }
          }
        }
        else complete(StatusCodes.Unauthorized)
      }
    }
  }
}
