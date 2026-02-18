package cloud.chlora.authservice.common.exception

import cloud.chlora.authservice.common.enums.AuthErrorCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import cloud.chlora.authservice.common.response.ErrorResponse

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntime(ex: RuntimeException): ResponseEntity<Map<String, String>> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(mapOf("message" to ex.message.orEmpty()))
    }

    @ExceptionHandler(AuthException.UserNotFoundException::class)
    fun handleUserNotFound(ex: AuthException.UserNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponse(AuthErrorCode.USER_NOT_FOUND.code, ex.message!!))
    }

    @ExceptionHandler(AuthException.InvalidCredentialsException::class)
    fun handleInvalidCredentials(ex: AuthException.InvalidCredentialsException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponse(AuthErrorCode.INVALID_CREDENTIALS.code, ex.message!!))
    }

    @ExceptionHandler(AuthException.EmailAlreadyRegisteredException::class)
    fun handleEmailExists(ex: AuthException.EmailAlreadyRegisteredException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResponse(AuthErrorCode.EMAIL_EXISTS.code, ex.message!!))
    }
}