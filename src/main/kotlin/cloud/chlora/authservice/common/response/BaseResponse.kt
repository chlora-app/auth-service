package cloud.chlora.authservice.common.response

import java.time.Instant

data class BaseResponse<T>(
    val message: String,
    val timestamp: Instant,
    val data: T
)
