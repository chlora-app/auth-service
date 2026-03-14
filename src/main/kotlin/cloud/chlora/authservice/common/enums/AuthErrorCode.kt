package cloud.chlora.authservice.common.enums

enum class AuthErrorCode(val code: String) {
    VALIDATION_ERROR("AUTH-400"),
    INVALID_CREDENTIALS("AUTH-401"),
    USER_NOT_FOUND("AUTH-404"),
    EMAIL_EXISTS("AUTH-409")
}