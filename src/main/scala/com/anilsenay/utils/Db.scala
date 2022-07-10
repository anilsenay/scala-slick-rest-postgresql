package com.anilsenay.utils

import slick.jdbc.{MySQLProfile, PostgresProfile}

import slick.jdbc.JdbcProfile

trait Profile {
 val profile: JdbcProfile
}

trait DatabaseModule1 { self: Profile =>
 import profile.api._
 val db: profile.backend.DatabaseFactoryDef = Database
}

class DatabaseLayer(val profile: JdbcProfile) extends Profile with DatabaseModule1

object PostgresDb {
 val db = new DatabaseLayer(PostgresProfile).db.forConfig("postgresDb")
}

object MysqlDb {
 val db = new DatabaseLayer(MySQLProfile).db.forConfig("mysqlDb")
}
