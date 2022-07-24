package com.anilsenay.controllers

import akka.http.scaladsl.server.Directives.authenticateOAuth2
import akka.http.scaladsl.server.directives.{AuthenticationDirective, Credentials}
import com.anilsenay.models.JWTContent
import com.anilsenay.services.AuthService

trait BaseAuthenticator {
  def jwtAuthenticator(credentials: Credentials): Option[JWTContent] = {
    credentials match {
      case p @ Credentials.Provided(token) if AuthService.validateToken(token) => Some(AuthService.decodeToken(token))
      case _ => None
    }
  }

  def authenticate: AuthenticationDirective[JWTContent] = authenticateOAuth2(realm = "secure site", jwtAuthenticator)
}
