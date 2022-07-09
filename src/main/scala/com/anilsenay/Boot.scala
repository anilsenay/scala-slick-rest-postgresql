package com.anilsenay

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.anilsenay.controllers.PersonController
import com.anilsenay.services.PersonService
import com.typesafe.scalalogging.StrictLogging
import slick.jdbc.PostgresProfile.api._

object Boot extends App with StrictLogging {
  private implicit val system = ActorSystem()
  private implicit val materializer = ActorMaterializer()
  private implicit val ec = system.dispatcher

  val db = Database.forConfig("postgresDb")

  val dbService = new PersonService(db)
  val restService = new PersonController(dbService)

  Http()
    .bindAndHandle(restService.route, "localhost", 8080)
    .map(_ => logger.info("Server started at port 8080"))
}
