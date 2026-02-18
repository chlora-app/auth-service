package cloud.chlora.authservice.repository

import cloud.chlora.authservice.domain.entity.User
import cloud.chlora.authservice.domain.enums.UserRole
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
class AuthRepository(private val jdbcClient: JdbcClient) {

    fun findByEmail(email: String): Optional<User> {
        val sql = "SELECT * FROM users WHERE email = ?"
        return jdbcClient.sql(sql)
            .param(email)
            .query(userRowMapper)
            .optional()
    }

    fun findByUserId(userId: String): Optional<User> {
        val sql = "SELECT * FROM users WHERE user_id = ?"
        return jdbcClient.sql(sql)
            .param(userId)
            .query(userRowMapper)
            .optional()
    }

    fun save(user: User): User {
        val sql = """
            INSERT INTO users (email, password, name, role)
            VALUES (:email, :password, :name, :role)
            RETURNING id, user_id, email, password, name, role, created_at, updated_at, deleted_at
            """.trimIndent()

        return jdbcClient.sql(sql)
            .param("email", user.email)
            .param("password", user.password)
            .param("name", user.name)
            .param("role", user.role.name)
            .query(userRowMapper)
            .single()
    }

    private val userRowMapper = RowMapper { rs, _ ->
        User(
            id = rs.getLong("id"),
            userId = rs.getString("user_id"),
            email = rs.getString("email"),
            password = rs.getString("password"),
            name = rs.getString("name"),
            role = UserRole.valueOf(rs.getString("role")),
            createdAt = rs.getTimestamp("created_at").toInstant(),
            updatedAt = rs.getTimestamp("updated_at")?.toInstant(),
            deletedAt = rs.getTimestamp("deleted_at")?.toInstant()
        )
    }
}