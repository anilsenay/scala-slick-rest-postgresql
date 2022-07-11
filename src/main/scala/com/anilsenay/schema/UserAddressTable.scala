package com.anilsenay.schema

import com.anilsenay.models.UserAddress
import com.anilsenay.schema.UsersTable.Users
import com.anilsenay.schema.AddressTable.Addresses
import slick.jdbc.PostgresProfile.api._

object UserAddressTable {

  class UserAddresses(tag: Tag) extends Table[UserAddress](tag, "user_address") {
    def userId = column[Option[String]]("user_id")
    def addressId = column[Option[String]]("address_id")

    def * =
      (userId, addressId) <> ((UserAddress.apply _).tupled, UserAddress.unapply)

    def user = foreignKey("fk_user", userId, TableQuery[Users])(_.id)
    def address = foreignKey("fk_user", addressId, TableQuery[Addresses])(_.id)
  }

  val userAddresses = TableQuery[UserAddresses]

}
