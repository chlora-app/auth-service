package cloud.chlora.authservice.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequest(

    @field:NotBlank(message = "Username or email is required")
    @field:Size(min = 5, max = 50, message = "Username or email must be between 5 and 50 characters")
    val userIdOrEmail: String,

    @field:NotBlank(message = "Password is required")
    val password: String
)
