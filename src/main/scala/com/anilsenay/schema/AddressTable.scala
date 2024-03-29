package com.anilsenay.schema

import com.anilsenay.models.Address
import com.anilsenay.utils.SelectedProfile.api._

object AddressTable {

  class Addresses(tag: Tag) extends Table[Address](tag, "address") {
    def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
    def title = column[String]("title")
    def city = column[String]("city")
    def region = column[String]("region")
    def zipcode = column[String]("zipcode")
    def fullAddress = column[String]("full_address")

    def * =
      (id, title, city, region, zipcode, fullAddress) <> ((Address.apply _).tupled, Address.unapply)

  }

  val addresses = TableQuery[Addresses]

}
