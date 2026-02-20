package io.github.eliasborchani.foundry.modules.authentication.application.dto

data class TokenPair(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
)
