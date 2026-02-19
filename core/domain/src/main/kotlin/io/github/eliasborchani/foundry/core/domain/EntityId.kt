package io.github.eliasborchani.foundry.core.domain

import java.util.UUID

/**
 * Base class for typed, value-object IDs.
 * Subclass this in each module's domain to get type-safe IDs that cannot be accidentally mixed.
 *
 * Example:
 *   @JvmInline value class UserId(val value: UUID) : EntityId(value)
 */
abstract class EntityId(open val value: UUID) {
    override fun toString(): String = value.toString()
    override fun equals(other: Any?): Boolean =
        other != null && other::class == this::class && (other as EntityId).value == value
    override fun hashCode(): Int = value.hashCode()
}
