package com.anilsenay.services

import com.anilsenay.models.{AuthResponse, JWTContent, User, UserPassword, UserRegister}
import com.anilsenay.schema.UsersTable._
import com.anilsenay.schema.UserPasswordTable._
import com.anilsenay.utils.{AuthException, SaltGenerator}
import com.anilsenay.utils.SelectedProfile.api._
import com.roundeights.hasher.Digest.digest2string

import scala.concurrent.Future
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import com.roundeights.hasher.Implicits._
import com.typesafe.config.ConfigFactory
import spray.json._

import java.time.Clock

object AuthService extends BaseService {

  val _90_DAYS_AS_SECOND = 7776000
  implicit val clock: Clock = Clock.systemUTC
  private val secretKey: String = ConfigFactory.load().getString("secretKey")

  private def createToken(id: Long, email: String, name: String, surname: String): String = {
    val contentAsJson = JWTContent(id, email, name, surname).toJson.toString()
    val JwtContent = JwtClaim({contentAsJson}).issuedNow.expiresIn(_90_DAYS_AS_SECOND)
    Jwt.encode(JwtContent, secretKey, JwtAlgorithm.HS256)
  }

  def validateToken: String => Boolean = (token: String) => {
    try {
      Jwt.isValid(token, secretKey, Seq(JwtAlgorithm.HS256))
    } catch {
      case e: Exception => false
    }
  }

  def login(email: String, password: String): Future[Option[AuthResponse]] = {
    db.run {
      for {
        user <- users.filter(_.email === email).result.head
        userPassword <- userPasswords.filter(_.userId === user.id).result.head
      } yield {
        if(userPassword.password == digest2string((password + userPassword.salt).sha256)) {
          val token = createToken(user.id.get, user.email, user.name, user.surname)
          Some(AuthResponse(user.id.get, user.email, token))
        } else {
          throw new AuthException()
        }
      }
    }
  }

  def register(user: UserRegister): Future[Some[AuthResponse]] = {
    val saltValue = SaltGenerator.get(20)
    val hashedPass = digest2string(user.password.salt(saltValue).sha256)
    val action = (users.map(user => (user.name, user.surname, user.email, user.phone)) returning users
      .map(_.id) into (
      (userData,
       id) => User(id, userData._1, userData._2, userData._3, userData._4)
      )) += (user.name, user.surname, user.email, user.phone.getOrElse(""))

    for {
      insertedUser <- db.run(action)
      userWithPass <- db.run((userPasswords returning userPasswords.map(_.userId)) += UserPassword(insertedUser.id, hashedPass, saltValue))
    } yield {
      println(userWithPass)
      if (userWithPass.nonEmpty && userWithPass.get > 0) {
        val token = createToken(insertedUser.id.get, insertedUser.email, insertedUser.name, insertedUser.surname)
        Some(AuthResponse(insertedUser.id.get, user.email, token))
      } else {
        throw new Exception()
      }
    }
  }
}