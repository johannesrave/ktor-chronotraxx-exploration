//package auth
//
//
//import io.ktor.server.auth.*
//import org.jetbrains.exposed.dao.Entity
//import org.jetbrains.exposed.dao.EntityClass
//import org.jetbrains.exposed.dao.id.EntityID
//import org.jetbrains.exposed.dao.with
//import org.jetbrains.exposed.sql.and
//import org.jetbrains.exposed.sql.transactions.transaction
//import persistence.Users
//import java.util.*
//
//class PostgresAccountRepository {
//    companion object {
//        fun create(username: String, password: String, email: String? = null): Account {
//
//            val entity = transaction {
//                AuthUserEntity.new {
//                    this.username = username
//                    this.email = email
//                    this.password = password
//                }
//            }
//            return entity.mapToAuthUser()
//        }
//
//        fun fetchById(id: UUID): Account? {
//            return transaction { AuthUserEntity.findById(id)?.mapToAuthUser() }
//        }
//
//        fun fetchByCredentials(creds: UserPasswordCredential): Account? {
//            return transaction {
//                AuthUserEntity.find {
//                    (Users.username eq creds.name) and (Users.password eq creds.password)
//                }.with(AuthUserEntity::profile)
//                    .map {
//                        it.mapToAuthUser(profileId = it.profile.id.value)
//                    }
//                    .singleOrNull()
//            }
//        }
//
//        fun fetchAll(): Collection<Account> {
//            return transaction { AuthUserEntity.all().map { it.mapToAuthUser() } }
//        }
//
//        fun update(user: Account): Account? {
//            return transaction { AuthUserEntity.findById(user.id) }?.let {
//                it.username = user.username
//                it.email = user.email
//                it.password = user.password
//                user
//            } ?: run { null }
//        }
//    }
//
//    class AuthUserEntity(id: EntityID<UUID>) : Entity<UUID>(id) {
//        companion object : EntityClass<UUID, AuthUserEntity>(Users)
//
//        var username by Users.username
//        var email by Users.email
//        var password by Users.password
//
//        fun mapToAuthUser(profileId: UUID? = null): Account {
//            return Account(
//                id = id.value,
//                username = username,
//                email = email,
//                password = password,
//            )
//        }
//    }
//}
//
//class Account(
//    val id: UUID,
//    val username: String,
//    val password: String,
//    val email: String? = null,
//    val profileId: UUID
//)
