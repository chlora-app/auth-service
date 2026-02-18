package cloud.chlora.authservice.common.exception

sealed class AuthException(message: String) : RuntimeException(message) {

    class UserNotFoundException(identifier: String) : AuthException(identifier)

    class InvalidCredentialsException : AuthException("Invalid credentials")

    class EmailAlreadyRegisteredException(val email: String) : AuthException("Email already registered")
}