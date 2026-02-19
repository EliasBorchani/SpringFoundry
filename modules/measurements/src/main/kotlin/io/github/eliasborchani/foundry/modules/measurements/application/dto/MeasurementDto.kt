package io.github.eliasborchani.foundry.modules.measurements.application.dto

import io.github.eliasborchani.foundry.modules.measurements.domain.Measurement
import java.time.Instant
import java.util.UUID

data class MeasurementDto(
    val id: UUID,
    val userId: UUID,
    val type: String,
    val value: Double,
    val unit: String,
    val recordedAt: Instant,
) {
    companion object {
        fun from(measurement: Measurement) = MeasurementDto(
            id = measurement.id,
            userId = measurement.userId,
            type = measurement.type,
            value = measurement.value,
            unit = measurement.unit,
            recordedAt = measurement.recordedAt,
        )
    }
}
