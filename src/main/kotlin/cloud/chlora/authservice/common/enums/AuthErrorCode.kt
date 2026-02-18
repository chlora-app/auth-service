package cloud.chlora.authservice.common.enums

enum class AuthErrorCode(val code: String) {
    USER_NOT_FOUND("AUTH-001"),
    INVALID_CREDENTIALS("AUTH-002"),
    EMAIL_EXISTS("AUTH-003")
}