package dev.jhyub

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

object Database {
    private val url = "jdbc:h2:~/skyst-sample.db;DB_CLOSE_DELAY=-1"
    private val user = "jhyub"
    private val driver = "org.h2.Driver"
    private val password = System.getenv("H2_PASSWORD") ?: "snucse"

    init {
        org.jetbrains.exposed.sql.Database.connect(url, driver, user, password)
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(Users, UserTokens, Repos)
        }
        println("Database connected")
    }
}

object Users : IntIdTable() {
    val name = varchar("name", length = 50)
    val email = varchar("email", length = 50).uniqueIndex()
    val passwordHash = varchar("password_hash", length = 64)
}

object UserTokens : IntIdTable() {
    val value = varchar("value", length = 64).uniqueIndex()
    val isExpired = bool("is_expired").default(false)
    val owner = reference("owner", Users)
}

object Repos : IntIdTable() {
    val name        = varchar("name", 100)
    val description = text("description").nullable()
    val content     = text("content")
    val createdBy   = reference("created_by", Users)
    val createdAt   = date("created_at").clientDefault { LocalDate.now() }
    val updatedAt   = date("updated_at").clientDefault { LocalDate.now() }
}
