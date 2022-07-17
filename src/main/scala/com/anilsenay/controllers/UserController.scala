package com.anilsenay.controllers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.anilsenay.models.{Address, User, UserWithAddress}
import com.anilsenay.services.{AddressService, UserService}
import com.typesafe.scalalogging.LazyLogging
import spray.json._

import scala.util.{Failure, Success}

class UserController(dbService: UserService.type) extends SprayJsonSupport with DefaultJsonProtocol with LazyLogging {
  val route: Route = pathPrefix("api" / "user") {
    get {
      pathEndOrSingleSlash {
        complete(dbService.getAllPeople)
      } ~
      path(LongNumber) {
        (userId) => {
          parameters("address".as[Boolean].withDefault(false)) { address =>
            if (address) {
              val q = dbService.getUserWithAddresses(userId)
              onComplete(q) {
                case Success(result) => {
                  result match {
                    case (_: Option[User], b: Option[UserWithAddress]) if b.isDefined => complete(result._2)
                    case _ => complete(JsObject(
                      "user" -> result._1.toJson,
                      "address" -> JsArray()
                    ))
                  }

                }
                case Failure(e) => {
                  logger.error(s"Failed to fetch user ${userId}", e)
                  complete(StatusCodes.InternalServerError)
                }
              }
            } else {
              complete(dbService.getUser(userId))
            }
          }
        }
      }
    } ~
    post {
      path(LongNumber / "address") {
        (userId) => {
        entity(as[Address]) { address =>
          val savedAddress = AddressService.insertUserAddress(userId, address.title, address.city, address.region, address.zipcode, address.fullAddress)
          onComplete(savedAddress) {
            case Success(savedAddress) => {
              logger.info(s"Inserted person with id:${savedAddress}")
              complete(JsObject("updatedRows" -> JsNumber(savedAddress)))
            }
            case Failure(e) => {
              logger.error(s"Failed to insert person address ${userId}", e)
              complete(StatusCodes.InternalServerError)
              }
            }
          }
      }
    } ~
      pathEndOrSingleSlash {
      entity(as[User]) { user =>
        val saved = dbService.insertUser(user.name, user.surname, user.email, user.phone)
        onComplete(saved) {
          case Success(savedUser) => {
            logger.info(s"Inserted person with id:${savedUser.id}")
            complete(savedUser)
          }
          case Failure(e) => {
            logger.error(s"Failed to insert a person ${user.id}", e)
            complete(StatusCodes.InternalServerError)
            }
          }
        }
      }
    } ~
    put {
      path(LongNumber) { id =>
        entity(as[User]) { user =>
          val updated = dbService.update(id, user)
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
