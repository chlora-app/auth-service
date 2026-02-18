package cloud.chlora.authservice.service

import cloud.chlora.authservice.common.exception.AuthException
import cloud.chlora.authservice.common.response.BaseResponse
import cloud.chlora.authservice.domain.entity.User
import cloud.chlora.authservice.domain.enums.UserRole
import cloud.chlora.authservice.dto.request.LoginRequest
import cloud.chlora.authservice.dto.request.RegisterRequest
import cloud.chlora.authservice.dto.response.RegisterResponse
import cloud.chlora.authservice.dto.result.LoginResult
import cloud.chlora.authservice.mapper.ResponseMapper
import cloud.chlora.authservice.repository.AuthRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.regex.Pattern

@Service
class AuthService(
    private val jwtEncoder: JwtEncoder,
    private val passwordEncoder: PasswordEncoder,
    private val authRepository: AuthRepository
) {

    private val log: Logger = LoggerFactory.getLogger(AuthService::class.java)

    fun login(request: LoginRequest): LoginResult {
        val identifier = request.userIdOrEmail.trim()
        val loginMethod = if (isLoggedInByEmail(identifier)) "email" else "userId"

        log.info("event=login_attempt login_method={} user_identifier={}", loginMethod, identifier)

        val userFinder = if (loginMethod == "email") authRepository::findByEmail else authRepository::findByUserId
        val user: User = userFinder(identifier).orElseThrow {
            log.warn("event=login_failed reason=user_not_found login_method={} user_identifier={}", loginMethod, identifier)
            AuthException.UserNotFoundException(identifier)
        }

        if (!passwordEncoder.matches(request.password, user.password)) {
            log.warn(
                "event=login_failed reason=invalid_password login_method={} user_identifier={} user_id={}",
                loginMethod, identifier, user.userId
            )
            throw AuthException.InvalidCredentialsException()
        }

        val token = generateAccessToken(user)

        log.info("event=login_success login_method={} user_identifier={} user_id={}", loginMethod, identifier, user.userId)
        return ResponseMapper.toLoginResult(user, "User ${user.userId} logged in successfully.", token)
    }

    fun register(request: RegisterRequest): BaseResponse<RegisterResponse> {
        val maskedEmail = maskEmail(request.email)

        log.info("event=register_attempt email={}", maskedEmail)

        val emailExists = authRepository.findByEmail(request.email).isPresent
        if (emailExists) {
            log.warn("event=register_failed reason=email_exists email={}", maskedEmail)
            throw AuthException.EmailAlreadyRegisteredException("Email exists")
        }

        val encodedPassword = passwordEncoder.encode(request.password)
        val user = User(
            name = request.name,
            email = request.email,
            password = requireNotNull(encodedPassword),
            role = UserRole.USER,
            createdAt = Instant.now()
        )

        val createdUser = authRepository.save(user)
        log.info("event=register_success userId={} email={}", createdUser.userId, maskedEmail)

        return ResponseMapper.toBaseRegisterResponse(createdUser, "User ${createdUser.userId} registered successfully.")
    }

    // ===== HELPER ===== //
    private fun generateAccessToken(user: User): String {
        val now = Instant.now()

        val headers = JwsHeader.with(SignatureAlgorithm.RS256).build()
        val claims = JwtClaimsSet.builder()
            .issuer("auth-service")
            .subject(user.userId)
            .claim("email", user.email)
            .claim("role", user.role)
            .issuedAt(now)
            .expiresAt(now.plusSeconds(60 * 60 * 8)) // 8 hours
            .build()

        return jwtEncoder.encode(JwtEncoderParameters.from(headers, claims)).tokenValue
    }

    private fun isLoggedInByEmail(input: String): Boolean {
        val emailPattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return emailPattern.matcher(input).matches()
    }

    private fun maskEmail(email: String): String {
        return email.replace("(^.).*(@.*$)".toRegex(), "$1***$2")
    }
}