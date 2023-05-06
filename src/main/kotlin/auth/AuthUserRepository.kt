package auth


import io.ktor.server.auth.*
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import persistence.Profiles
import persistence.Users
import java.util.*

class AuthUserPostgresRepository {
    companion object {
        fun create(username: String, password: String, email: String? = null): AuthUser {

            val entity = transaction {
                AuthUserEntity.new {
                    this.username = username
                    this.email = email
                    this.password = password
                    this.profile = ProfileEntity.new {
                        this.name = username
                        this.description = "Waste your time on my lame profile!"
                    }
                }
            }
            return entity.mapToAuthUser()
        }

        fun fetchById(id: UUID): AuthUser? {
            return transaction { AuthUserEntity.findById(id)?.mapToAuthUser() }
        }

        fun fetchByCredentials(creds: UserPasswordCredential): AuthUser? {
            return transaction {
                AuthUserEntity.find {
                    (Users.username eq creds.name) and (Users.password eq creds.password)
                }.with(AuthUserEntity::profile)
                    .map {
                        it.mapToAuthUser(profileId = it.profile.id.value)
                    }
                    .singleOrNull()
            }
        }

        fun fetchAll(): Collection<AuthUser> {
            return transaction { AuthUserEntity.all().map { it.mapToAuthUser() } }
        }

        fun update(user: AuthUser): AuthUser? {
            return transaction { AuthUserEntity.findById(user.id) }?.let {
                it.username = user.username
                it.email = user.email
                it.password = user.password
                user
            } ?: run { null }
        }
    }

    class AuthUserEntity(id: EntityID<UUID>) : Entity<UUID>(id) {
        companion object : EntityClass<UUID, AuthUserEntity>(Users)

        var username by Users.username
        var email by Users.email
        var password by Users.password
        var profile by ProfileEntity referencedOn Users.profile

        fun mapToAuthUser(profileId: UUID? = null): AuthUser {
            val _profileId = profileId ?: transaction { profile.id.value }
            return AuthUser(
                id = id.value,
                username = username,
                email = email,
                password = password,
                profileId = _profileId
            )
        }
    }

    class ProfileEntity(id: EntityID<UUID>) : Entity<UUID>(id) {
        companion object : EntityClass<UUID, ProfileEntity>(Profiles)

        var name by Profiles.name
        var description by Profiles.description
    }
}

class AuthUser(
    val id: UUID,
    val username: String,
    val password: String,
    val email: String? = null,
    val profileId: UUID
)
