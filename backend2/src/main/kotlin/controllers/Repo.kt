package dev.jhyub.controllers

import dev.jhyub.Repos
import dev.jhyub.UserTokens
import dev.jhyub.Users
import dev.jhyub.models.Repo
import dev.jhyub.models.User
import dev.jhyub.models.UserToken
import dev.jhyub.sha256
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.typeInfo
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import javax.swing.UIManager.put
import java.time.LocalDate



fun Route.repo() {
    route(path = "/api/repos") {
        get {
            val token = call.request.queryParameters["token"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "token required"))
            val userId = token.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "invalid token"))
            val user = transaction {
                User.findById(userId)
            } ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "user not found"))

            val repos = transaction {
                user.repos
                    .map { row ->
                        mapOf(
                            "id" to row.id.value,
                            "name" to row.name,
                            "description" to row.description,
                            "content" to row.content,
                            "created_by" to row.createdBy.value,
                            "created_at" to row.createdAt.toString(),
                            "updated_at" to row.updatedAt.toString()
                        )
                    }
            }

        }

        get("/api/repos/get") {
            login() { user, _ ->
                val repoId = call.parameters["repo_id"]?.toIntOrNull()
                    ?: return@login call.respond(HttpStatusCode.BadRequest, "Invalid repo_id")

                val repos = transaction { Repo.findById(repoId) }
                if (repos == null || repos.createdBy.value != user.id.value) {
                    call.respond(HttpStatusCode.NotFound, "Repo not found or no permission")
                } else {
                    call.respond(HttpStatusCode.OK)
                }
            }
        }

        post("/api/repos/create") {
            login { user, params ->
                val name = params["name"]
                    ?: return@login call.respond(HttpStatusCode.BadRequest, "Missing 'name' parameter")
                val description = params["description"]
                val content = params["content"]
                    ?: return@login call.respond(HttpStatusCode.BadRequest, "Missing 'content' parameter")

                val newId = transaction {
                    Repo.new {
                        this.name = name
                        this.description = description
                        this.content = content
                        this.createdBy = user.id
                        this.createdAt = LocalDate.now()
                        this.updatedAt = LocalDate.now()
                    }.id.value
                }
                call.respond(HttpStatusCode.Created, mapOf("id" to newId))
            }
        }



    }
}