package cloud.chlora.authservice.controller

import cloud.chlora.authservice.common.exception.GlobalExceptionHandler
import cloud.chlora.authservice.common.response.BaseResponse
import cloud.chlora.authservice.domain.enums.UserRole
import cloud.chlora.authservice.dto.request.RegisterRequest
import cloud.chlora.authservice.dto.response.RegisterResponse
import cloud.chlora.authservice.service.AuthService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.time.Instant

@ActiveProfiles("test")
@Import(GlobalExceptionHandler::class)
@WebMvcTest(AuthController::class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    private val objectMapper = jacksonObjectMapper()

    @MockitoBean
    lateinit var authService: AuthService

    @Test
    fun `should return 201 when register success`() {
        val request = RegisterRequest(
            name = "John Doe",
            email = "john@example.com",
            password = "Password123",
            confirmPassword = "Password123"
        )
        println("Request: $request")

        val response = BaseResponse(
            data = RegisterResponse(
                userId = "USR123",
                name = "John Doe",
                email = "john@example.com",
                role = UserRole.USER,
                createdAt = Instant.now()
            ),
            timestamp = Instant.now(),
            message = "success"
        )

        whenever(authService.register(request))
            .thenReturn(response)

        mockMvc.post("/api/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isCreated() }
                jsonPath("$.data.userId") { value("USR123") }
                jsonPath("$.data.email") { value("john@example.com") }
            }
    }

    @Test
    fun `should return 400 when validation fails`() {
        val request = RegisterRequest(
            name = "",
            email = "invalid-email",
            password = "123",
            confirmPassword = "456"
        )

        mockMvc.post("/api/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `should propagate service exception`() {
        val request = RegisterRequest(
            name = "John Doe",
            email = "john@example.com",
            password = "Password123",
            confirmPassword = "Password123"
        )

        whenever(authService.register(request))
            .thenThrow(RuntimeException("Email exists"))

        mockMvc.post("/api/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isInternalServerError() }
            }
    }
}