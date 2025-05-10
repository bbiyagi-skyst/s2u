package dev.jhyub.models

import dev.jhyub.Repos
import dev.jhyub.UserTokens
import dev.jhyub.Users
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object: IntEntityClass<User>(Users)

    var name by Users.name
    var email by Users.email
    var password by Users.passwordHash
    val tokens by UserToken referrersOn UserTokens.owner
    val repos by Repo referrersOn Repos.createdBy
}
