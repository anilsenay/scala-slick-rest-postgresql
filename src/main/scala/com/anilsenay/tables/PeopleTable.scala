package com.anilsenay.tables

import com.anilsenay.models.Person
import slick.jdbc.PostgresProfile.api._

object PeopleTable {
  class People(tag: Tag) extends Table[Person](tag, "person_list") {
    def id = column[Option[Long]]("person_id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("person_name")

    def surname = column[String]("person_surname")

    def * =
      (id, name, surname) <> ((Person.apply _).tupled, Person.unapply)
  }
  val people = TableQuery[People]

}
