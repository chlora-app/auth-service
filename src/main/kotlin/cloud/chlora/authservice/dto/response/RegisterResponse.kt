package cloud.chlora.authservice.dto.response

import cloud.chlora.authservice.domain.enums.UserRole
import java.time.Instant

data class RegisterResponse(
    val userId: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val createdAt: Instant
)
