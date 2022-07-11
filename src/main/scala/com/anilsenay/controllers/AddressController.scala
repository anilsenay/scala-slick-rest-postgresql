package com.anilsenay.controllers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.anilsenay.models.Address
import com.anilsenay.services.AddressService
import com.typesafe.scalalogging.LazyLogging
import spray.json._

import scala.util.{Failure, Success}

class AddressController(dbService: AddressService) extends SprayJsonSupport with DefaultJsonProtocol with LazyLogging {
  val route: Route = pathPrefix("api" / "address") {
    get {
      pathEndOrSingleSlash {
        complete(dbService.getAllAddress)
      } ~
        path(Segment) { addressId =>
          complete(dbService.getAddress(addressId))
        }
    } ~
    post {
      pathEndOrSingleSlash {
        entity(as[Address]) { address =>
          val saved = dbService.insertAddress(address.title, address.city, address.region, address.zipcode, address.fullAddress)
          onComplete(saved) {
            case Success(savedAddress) => {
              logger.info(s"Inserted person with id:${savedAddress.id}")
              complete(savedAddress)
            }
            case Failure(e) => {
              logger.error(s"Failed to insert a person ${address.id}", e)
              complete(StatusCodes.InternalServerError)
            }
          }
        }
      }
    } ~
    put {
      path(Segment) { id =>
        entity(as[Address]) { address =>
          val updated = dbService.update(id, address)
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
      path(Segment) { id =>
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
