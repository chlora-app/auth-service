package cloud.chlora.authservice.security.config

import cloud.chlora.authservice.security.util.CookieAuth
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig {

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        cookieBearerTokenResolver: BearerTokenResolver
    ): SecurityFilterChain {

        http.cors {}
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .csrf { it.disable() }
            .cors {  }
            .authorizeHttpRequests {
                it.requestMatchers("/actuator/health", "/actuator/prometheus").permitAll()
                it.requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                it.anyRequest().authenticated()
            }
            .oauth2ResourceServer { rs ->
                rs.bearerTokenResolver(cookieBearerTokenResolver)
                rs.jwt(Customizer.withDefaults())
            }

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
//        val config = CorsConfiguration()
//        config.allowedOriginPatterns = listOf("https://chlora.cloud")
//        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
//        config.allowedHeaders = listOf("*")
//        config.allowCredentials = true
//
//        val source = UrlBasedCorsConfigurationSource()
//        source.registerCorsConfiguration("/**", config)
//
//        return source

        val config = CorsConfiguration()
        config.allowedOrigins = listOf("*")
        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
        config.allowedHeaders = listOf("*")
        config.allowCredentials = false

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)

        return source
    }

    @Bean
    fun cookieBearerTokenResolver(): BearerTokenResolver {
        return BearerTokenResolver { request ->
            request.cookies
                ?.firstOrNull { it.name == CookieAuth.ACCESS_TOKEN_COOKIE }
                ?.value
        }
    }
}

