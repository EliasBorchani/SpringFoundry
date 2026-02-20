package io.github.eliasborchani.foundry.modules.measurements.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "measurements")
class MeasurementEntity(
    @Id
    val id: UUID = UUID.randomUUID(),

    /** Reference to a user by ID. No FK — cross-module references are by ID only. */
    @Column(nullable = false)
    val userId: UUID,

    @Column(nullable = false)
    val type: String,

    @Column(nullable = false)
    val value: Double,

    @Column(nullable = false, length = 20)
    val unit: String,

    @Column(nullable = false, updatable = false)
    val recordedAt: Instant = Instant.now(),
)
