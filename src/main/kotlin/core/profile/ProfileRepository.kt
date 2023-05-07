//package core.profile
//
//import org.jetbrains.exposed.dao.Entity
//import org.jetbrains.exposed.dao.EntityClass
//import org.jetbrains.exposed.dao.id.EntityID
//import org.jetbrains.exposed.sql.transactions.transaction
//import persistence.Profiles
//import java.util.*
//
//class ProfileRepository {
//    companion object {
//        fun fetchById(id: UUID): Profile? {
//            return transaction { ProfileEntity.findById(id)?.mapToProfile() }
//        }
//
//        fun fetchAll(): Collection<Profile> {
//            return transaction { ProfileEntity.all().map { it.mapToProfile() } }
//        }
//
//        fun update(profile: Profile): Profile? {
//            return transaction { ProfileEntity.findById(profile.id) }?.let {
//                it.name = profile.name
//                it.surname = profile.surname
//                it.description = profile.description
//                it.picture = profile.picture
//                profile
//            } ?: run { null }
//        }
//    }
//
//    class ProfileEntity(id: EntityID<UUID>) : Entity<UUID>(id) {
//        companion object : EntityClass<UUID, ProfileEntity>(Profiles)
//
//        var name by Profiles.name
//        var surname by Profiles.surname
//        var description by Profiles.description
//        var picture by Profiles.picture
//
//        fun mapToProfile(): Profile = Profile(
//            id = id.value,
//            name = name,
//            surname = surname,
//            description = description,
//            picture = picture
//        )
//    }
//}
//
//class Profile(
//    val id: UUID,
//    val name: String,
//    val surname: String?,
//    val description: String,
//    val picture: String?
//)
