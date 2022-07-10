package com.anilsenay.services

import com.anilsenay.models.Person
import slick.jdbc.PostgresProfile.api._
import com.anilsenay.schema.PeopleTable._

import scala.concurrent.{ExecutionContext, Future}

class PersonService(db: Database)(implicit ec: ExecutionContext) {
  def getAllPeople: Future[Seq[Person]] = db.run(people.result)

  def getPerson(id: Long): Future[Option[Person]] = {
    db.run {
      people.filter(person => person.id === id).result.headOption
    }
  }

  def insertPerson(name: String, surname: String): Future[Person] = {
    val action = (people.map(person => (person.name, person.surname)) returning people
      .map(_.id) into (
      (nameAndSurname,
       id) => Person(id, nameAndSurname._1, nameAndSurname._2)
      )) += (name, surname)
    db.run(action)
  }

  def update(id: Long, name: String, surname: String): Future[Int] = {
    db.run {
      people
        .filter(person => person.id === id)
        .map(person => (person.id, person.name, person.surname))
        .update((Some(id), name, surname))
    }
  }
}