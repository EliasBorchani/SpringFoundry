package io.github.eliasborchani.foundry.modules.authentication.application

import io.github.eliasborchani.foundry.modules.authentication.application.dto.TokenPair
import io.github.eliasborchani.foundry.modules.authentication.domain.CredentialEntity
import io.github.eliasborchani.foundry.modules.authentication.domain.RefreshTokenEntity
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.Date
import java.util.UUID

@Service
@Transactional(readOnly = true)
class AuthServiceImpl(
    private val credentialRepository: CredentialRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    @Value("\${auth.jwt.secret}") private val jwtSecret: String,
    @Value("\${auth.jwt.access-token-ttl-seconds:900}") private val accessTokenTtlSeconds: Long,
    @Value("\${auth.jwt.refresh-token-ttl-days:7}") private val refreshTokenTtlDays: Long,
) : AuthService {

    private val passwordEncoder = BCryptPasswordEncoder()

    private val signingKey by lazy {
        Keys.hmacShaKeyFor(jwtSecret.toByteArray(Charsets.UTF_8))
    }

    @Transactional
    override fun register(userId: UUID, email: String, rawPassword: String) {
        require(credentialRepository.findByEmail(email) == null) {
            "Credentials for $email already exist"
        }
        val credential = CredentialEntity(
            email = email,
            passwordHash = passwordEncoder.encode(rawPassword)!!,
            userId = userId,
        )
        credentialRepository.save(credential)
    }

    @Transactional
    override fun login(email: String, rawPassword: String): TokenPair {
        val credential = credentialRepository.findByEmail(email)
            ?: throw IllegalArgumentException("Invalid credentials")
        require(passwordEncoder.matches(rawPassword, credential.passwordHash)) {
            "Invalid credentials"
        }
        return issueTokenPair(credential.userId, credential.email)
    }

    @Transactional
    override fun refresh(refreshToken: String): TokenPair {
        val stored = refreshTokenRepository.findByToken(refreshToken)
            ?: throw IllegalArgumentException("Invalid refresh token")
        require(!stored.revoked) { "Refresh token has been revoked" }
        require(stored.expiresAt.isAfter(Instant.now())) { "Refresh token has expired" }

        // Rotate: revoke the old token before issuing a new pair
        stored.revoked = true
        refreshTokenRepository.save(stored)

        return issueTokenPair(stored.userId, stored.email)
    }

    @Transactional
    override fun revoke(refreshToken: String) {
        val stored = refreshTokenRepository.findByToken(refreshToken) ?: return
        stored.revoked = true
        refreshTokenRepository.save(stored)
    }

    private fun issueTokenPair(userId: UUID, email: String): TokenPair {
        val now = Instant.now()
        val accessToken = Jwts.builder()
            .subject(userId.toString())
            .claim("email", email)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(accessTokenTtlSeconds)))
            .signWith(signingKey)
            .compact()

        val rawRefreshToken = UUID.randomUUID().toString()
        val refreshToken = RefreshTokenEntity(
            token = rawRefreshToken,
            userId = userId,
            email = email,
            expiresAt = now.plusSeconds(refreshTokenTtlDays * 24 * 3600),
        )
        refreshTokenRepository.save(refreshToken)

        return TokenPair(
            accessToken = accessToken,
            refreshToken = rawRefreshToken,
            expiresIn = accessTokenTtlSeconds,
        )
    }
}
