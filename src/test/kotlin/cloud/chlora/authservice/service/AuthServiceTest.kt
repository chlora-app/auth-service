package cloud.chlora.authservice.service

import cloud.chlora.authservice.domain.entity.User
import cloud.chlora.authservice.dto.request.RegisterRequest
import cloud.chlora.authservice.repository.AuthRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import java.util.Optional
import org.mockito.kotlin.*

class AuthServiceTest {

    private lateinit var jwtEncoder: JwtEncoder
    private lateinit var authRepository: AuthRepository
    private lateinit var passwordEncoder: PasswordEncoder
    private lateinit var authService: AuthService

    @BeforeEach
    fun setup() {
        jwtEncoder = mock()
        authRepository = mock()
        passwordEncoder = mock()

        authService = AuthService(jwtEncoder, passwordEncoder, authRepository)
    }

    @Test
    fun `register should succeed when email not exists`() {
        val request = validRequest()

        whenever(authRepository.findByEmail(request.email))
            .thenReturn(Optional.empty())

        whenever(passwordEncoder.encode(request.password))
            .thenReturn("encodedPassword")

        whenever(authRepository.save(any()))
            .thenAnswer {
                val user = it.arguments[0] as User
                user.copy(id = 1L, userId = "USR123")
            }

        val response = authService.register(request)

        assertNotNull(response)
        verify(authRepository).save(any())
        verify(passwordEncoder).encode(request.password)
    }

    @Test
    fun `register should throw exception when email already exists`() {
        val request = validRequest()

        whenever(authRepository.findByEmail(request.email))
            .thenReturn(Optional.of(mock()))

        assertThrows<Exception> {
            authService.register(request)
        }

        verify(authRepository, never()).save(any())
        verify(passwordEncoder, never()).encode(any())
    }

    @Test
    fun `register should throw exception when password encoder returns null`() {
        val request = validRequest()

        whenever(authRepository.findByEmail(request.email))
            .thenReturn(Optional.empty())

        whenever(passwordEncoder.encode(request.password))
            .thenReturn(null)

        assertThrows<IllegalArgumentException> {
            authService.register(request)
        }

        verify(authRepository, never()).save(any())
    }

    @Test
    fun `register should propagate repository exception`() {
        val request = validRequest()

        whenever(authRepository.findByEmail(request.email))
            .thenReturn(Optional.empty())

        whenever(passwordEncoder.encode(request.password))
            .thenReturn("encodedPassword")

        whenever(authRepository.save(any()))
            .thenThrow(RuntimeException("DB error"))

        assertThrows<RuntimeException> {
            authService.register(request)
        }
    }

    @Test
    fun `saved password must be encoded`() {
        val request = validRequest()

        whenever(authRepository.findByEmail(request.email))
            .thenReturn(Optional.empty())

        whenever(passwordEncoder.encode(request.password))
            .thenReturn("encodedPassword")

        whenever(authRepository.save(any()))
            .thenAnswer {
                val user = it.arguments[0] as User
                user.copy(id = 1L, userId = "USR123")
            }

        authService.register(request)

        argumentCaptor<User>().apply {
            verify(authRepository).save(capture())
            assertEquals("encodedPassword", firstValue.password)
        }
    }

    private fun validRequest(): RegisterRequest {
        return RegisterRequest(
            name = "John Doe",
            email = "john@example.com",
            password = "Password123",
            confirmPassword = "Password123"
        )
    }
}
