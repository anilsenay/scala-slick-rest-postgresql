package com.anilsenay.controllers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.anilsenay.models.Person
import com.anilsenay.services.PersonService
import com.typesafe.scalalogging.LazyLogging
import spray.json._

import scala.util.{Failure, Success}
class PersonController(dbService: PersonService) extends SprayJsonSupport with DefaultJsonProtocol with LazyLogging {
  val route: Route = pathPrefix("api" / "person") {
    get {
      pathEndOrSingleSlash {
        complete(dbService.getAllPeople)
      } ~
        path(LongNumber) { personId =>
          complete(dbService.getPerson(personId))
        }
    } ~
    post {
      pathEndOrSingleSlash {
        entity(as[Person]) { person =>
          val saved = dbService.insertPerson(person.name, person.surname)
          onComplete(saved) {
            case Success(savedPerson) => {
              logger.info(s"Inserted person with id:${savedPerson.id}")
              complete(savedPerson)
            }
            case Failure(e) => {
              logger.error(s"Failed to insert a person ${person.id}", e)
              complete(StatusCodes.InternalServerError)
            }
          }
        }
      }
    } ~
    put {
      path(LongNumber) { id =>
        entity(as[Person]) { person =>
          val updated = dbService.update(id, person.name, person.surname)
          onComplete(updated) {
            case Success(updatedRows) => complete(JsObject("updatedRows" -> JsNumber(updatedRows)))
            case Failure(e) => {
              logger.error(s"Failed to update a person ${id}", e)
              complete(StatusCodes.InternalServerError)
            }
          }
        }
      }
    }
  }
}
