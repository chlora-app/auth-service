package cloud.chlora.authservice.security.util

import org.springframework.http.ResponseCookie
import java.time.Duration

object CookieAuth {

    const val ACCESS_TOKEN_COOKIE = "access_token"

    fun accessTokenCookie(token: String): String {
        return ResponseCookie.from(ACCESS_TOKEN_COOKIE, token)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .sameSite("Lax")
            .maxAge(Duration.ofHours(8))
            .build()
            .toString()
    }

    fun clearAccessTokenCookie(): String {
        return ResponseCookie.from(ACCESS_TOKEN_COOKIE, "")
            .httpOnly(true)
            .secure(true)
            .path("/")
            .sameSite("Lax")
            .maxAge(Duration.ZERO)
            .build()
            .toString()
    }
}