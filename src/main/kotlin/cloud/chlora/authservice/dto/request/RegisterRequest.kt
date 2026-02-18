package cloud.chlora.authservice.dto.request

import cloud.chlora.authservice.validation.annotation.PasswordMatch
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

@PasswordMatch
data class RegisterRequest(

    @field:NotBlank(message = "Name is required")
    @field:Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    @field:Pattern(regexp = "^[A-Za-z ]+$", message = "Name must contain only letters and spaces")
    val name: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email format is invalid")
    val email: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    val password: String,

    @field:NotBlank(message = "Confirm password is required")
    @field:Size(min = 8, max = 50, message = "Confirm password must be between 8 and 50 characters")
    val confirmPassword: String
)
