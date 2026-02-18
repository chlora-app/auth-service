package cloud.chlora.authservice.dto.response

import cloud.chlora.authservice.domain.enums.UserRole

data class LoginResponse(
    val name: String,
    val email: String,
    val role: UserRole
)
