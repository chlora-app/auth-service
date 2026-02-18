package cloud.chlora.authservice.controller

import cloud.chlora.authservice.common.response.BaseResponse
import cloud.chlora.authservice.dto.request.LoginRequest
import cloud.chlora.authservice.dto.request.RegisterRequest
import cloud.chlora.authservice.dto.response.LoginResponse
import cloud.chlora.authservice.dto.response.RegisterResponse
import cloud.chlora.authservice.dto.result.LoginResult
import cloud.chlora.authservice.security.util.CookieAuth
import cloud.chlora.authservice.service.AuthService
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest, response: HttpServletResponse): ResponseEntity<BaseResponse<LoginResponse>> {
        val result: LoginResult = authService.login(request)
        response.addHeader(
            HttpHeaders.SET_COOKIE,
            CookieAuth.accessTokenCookie(result.token)
        )

        return ResponseEntity.ok(result.response)
    }

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<BaseResponse<RegisterResponse>> {
        val response: BaseResponse<RegisterResponse> = authService.register(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/logout")
    fun logout(response: HttpServletResponse): ResponseEntity<Void> {
        response.addHeader(
            HttpHeaders.SET_COOKIE,
            CookieAuth.clearAccessTokenCookie()
        )

        return ResponseEntity.noContent().build()
    }
}