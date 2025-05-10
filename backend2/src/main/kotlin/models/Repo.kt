package dev.jhyub.models

import dev.jhyub.Repos
import dev.jhyub.Users
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Repo(id: EntityID<Int>) : IntEntity(id) {
    companion object: IntEntityClass<Repo>(Repos)

    var name by Repos.name
    var description by Repos.description
    var content by Repos.content
    var createdBy by Repos.createdBy
    var createdAt by Repos.createdAt
    var updatedAt by Repos.updatedAt

}
