package com.anilsenay.schema

import com.anilsenay.models.User
import com.anilsenay.utils.SelectedProfile.api._

object UsersTable {

  class Users(tag: Tag) extends Table[User](tag, "users") {
    def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def surname = column[String]("surname")
    def email = column[String]("email")
    def phone = column[Option[String]]("phone")
    def isAdmin = column[Option[Boolean]]("is_admin")

    def * =
      (id, name, surname, email, phone, isAdmin) <> ((User.apply _).tupled, User.unapply)
  }

  val users = TableQuery[Users]

}
