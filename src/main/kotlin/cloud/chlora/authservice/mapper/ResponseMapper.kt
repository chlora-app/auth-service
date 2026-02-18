package cloud.chlora.authservice.mapper

import cloud.chlora.authservice.common.response.BaseResponse
import cloud.chlora.authservice.domain.entity.User
import cloud.chlora.authservice.dto.response.LoginResponse
import cloud.chlora.authservice.dto.response.RegisterResponse
import cloud.chlora.authservice.dto.result.LoginResult
import java.time.Instant

object ResponseMapper {

    fun toBaseRegisterResponse(user: User, message: String): BaseResponse<RegisterResponse> {
        val response =  RegisterResponse(
            userId = user.userId!!,
            name = user.name,
            email = user.email,
            role = user.role,
            createdAt = user.createdAt
        )

        return BaseResponse(
            message = message,
            timestamp = Instant.now(),
            data = response
        )
    }

    fun toBaseLoginResponse(user: User, message: String): BaseResponse<LoginResponse> {
        val response = LoginResponse(
            user.name,
            user.email,
            user.role
        )

        return BaseResponse(
            message = message,
            timestamp = Instant.now(),
            data = response
        )
    }

    fun toLoginResult(user: User, message: String, token: String): LoginResult {
        return LoginResult(toBaseLoginResponse(user, message), token)
    }
}