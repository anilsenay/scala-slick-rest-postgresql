package com.anilsenay.controllers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.anilsenay.models.Address
import com.anilsenay.services.ProductService
import com.typesafe.scalalogging.LazyLogging
import spray.json._

import scala.util.{Failure, Success}

class ProductController(dbService: ProductService) extends SprayJsonSupport with DefaultJsonProtocol with LazyLogging {
  val route: Route = pathPrefix("api" / "product") {
    get {
      pathEndOrSingleSlash {
        println("test")
        complete(dbService.getAllProducts)
      } ~
      path(Segment) {
        (productId) => {
          val q = dbService.getProduct(productId)
          onComplete(q) {
            case Success(result) => {
              println(result)
              complete(result)
            }
            case Failure(e) => {
              println(e)
              complete(StatusCodes.InternalServerError)
            }
          }
        }
      }
    }
  }
}
