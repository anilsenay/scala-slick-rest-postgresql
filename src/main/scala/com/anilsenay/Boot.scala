package com.anilsenay

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.anilsenay.controllers.RootRoute
import com.typesafe.scalalogging.StrictLogging

object Boot extends App with StrictLogging {
  private implicit val system = ActorSystem()
  private implicit val materializer = ActorMaterializer()
  private implicit val ec = system.dispatcher

  val restService = RootRoute()

  Http()
    .bindAndHandle(restService, "localhost", 8080)
    .map(_ => logger.info("Server started at port 8080"))
}
