package com.anilsenay.schema

import com.anilsenay.models.UserPassword
import com.anilsenay.schema.UsersTable.Users
import com.anilsenay.utils.SelectedProfile.api._

object UserPasswordTable {

  class UserPasswords(tag: Tag) extends Table[UserPassword](tag, "user_password") {
    def userId = column[Option[Long]]("user_id")
    def password = column[String]("password")
    def salt = column[String]("salt")

    def * =
      (userId, password, salt) <> ((UserPassword.apply _).tupled, UserPassword.unapply)

    def user = foreignKey("fk_user", userId, TableQuery[Users])(_.id)
  }

  val userPasswords = TableQuery[UserPasswords]
}
