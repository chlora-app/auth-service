package cloud.chlora.authservice.security.config

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

@Configuration
class JwtConfig(
    @Value($$"${security.jwt.public-key}") private val publicKeyResource: Resource,
    @Value($$"${security.jwt.private-key}") private val privateKeyResource: Resource
) {

    val privateKeyPem: String = String(privateKeyResource.inputStream.readAllBytes())
    val publicKeyPem: String = String(publicKeyResource.inputStream.readAllBytes())

    @Bean
    fun jwtDecoder(): JwtDecoder {
        val publicKey = parsePublicKey(publicKeyPem)
        return NimbusJwtDecoder.withPublicKey(publicKey).build()
    }

    @Bean
    fun jwtEncoder(): JwtEncoder {
        val publicKey = parsePublicKey(publicKeyPem)
        val privateKey = parsePrivateKey(privateKeyPem)

        val jwk = RSAKey.Builder(publicKey).privateKey(privateKey).build()
        val jwkSet = JWKSet(jwk)

        return NimbusJwtEncoder(ImmutableJWKSet(jwkSet))
    }

    private fun parsePublicKey(pem: String): RSAPublicKey {
        val clean = pem
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\\s".toRegex(), "")
        val bytes = Base64.getDecoder().decode(clean)
        val spec = X509EncodedKeySpec(bytes)
        return KeyFactory.getInstance("RSA").generatePublic(spec) as RSAPublicKey
    }

    private fun parsePrivateKey(pem: String): RSAPrivateKey {
        val clean = pem
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\s".toRegex(), "")
        val bytes = Base64.getDecoder().decode(clean)
        val spec = PKCS8EncodedKeySpec(bytes)
        return KeyFactory.getInstance("RSA").generatePrivate(spec) as RSAPrivateKey
    }
}