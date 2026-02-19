package io.github.eliasborchani.foundry.core.web

import java.time.Instant

/**
 * Standardised error body returned by all app controllers on failure.
 * Returned as JSON by a @ControllerAdvice / @ExceptionHandler in each app.
 */
data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val timestamp: Instant = Instant.now(),
)
