package cloud.chlora.authservice.dto.result

import cloud.chlora.authservice.common.response.BaseResponse
import cloud.chlora.authservice.dto.response.LoginResponse

data class LoginResult(
    val response: BaseResponse<LoginResponse>,
    val token: String
)