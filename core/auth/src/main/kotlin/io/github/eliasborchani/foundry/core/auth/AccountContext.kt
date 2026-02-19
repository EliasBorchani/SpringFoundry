package io.github.eliasborchani.foundry.core.auth

import java.util.UUID

/**
 * Holds the identity of the currently authenticated user for the duration of a request.
 *
 * Populated by a Spring Security filter or HandlerMethodArgumentResolver and injected
 * into controllers via @AuthenticationPrincipal or a custom ArgumentResolver.
 *
 * Modules must NOT depend on this class — it is only consumed by apps/controllers.
 */
data class AccountContext(
    val userId: UUID,
    val email: String,
    val roles: Set<String> = emptySet(),
) {
    fun hasRole(role: String): Boolean = role in roles
}
