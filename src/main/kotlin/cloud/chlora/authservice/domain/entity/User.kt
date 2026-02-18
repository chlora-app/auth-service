package cloud.chlora.authservice.domain.entity

import cloud.chlora.authservice.domain.enums.UserRole
import java.time.Instant

data class User(
    val id: Long? = null,
    val userId: String? = null,

    val email: String,
    val password: String,

    val name: String,
    val role: UserRole,

    val createdAt: Instant,
    val updatedAt: Instant? = null,
    val deletedAt: Instant? = null
)