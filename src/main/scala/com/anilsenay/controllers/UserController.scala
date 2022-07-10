package com.anilsenay.controllers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.anilsenay.models.User
import com.anilsenay.services.UserService
import com.typesafe.scalalogging.LazyLogging
import spray.json._

import scala.util.{Failure, Success}

class UserController(dbService: UserService) extends SprayJsonSupport with DefaultJsonProtocol with LazyLogging {
  val route: Route = pathPrefix("api" / "user") {
    get {
      pathEndOrSingleSlash {
        complete(dbService.getAllPeople)
      } ~
        path(Segment) { userId =>
          complete(dbService.getUser(userId))
        }
    } ~
    post {
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
      path(Segment) { id =>
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
