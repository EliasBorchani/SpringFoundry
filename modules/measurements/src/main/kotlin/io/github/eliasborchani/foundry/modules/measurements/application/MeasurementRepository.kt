package io.github.eliasborchani.foundry.modules.measurements.application

import io.github.eliasborchani.foundry.modules.measurements.domain.Measurement
import java.util.UUID

/**
 * Outbound port: data-access contract used by [MeasurementService].
 * Implemented in the infrastructure layer by [MeasurementRepositoryAdapter].
 */
interface MeasurementRepository {
    fun findById(id: UUID): Measurement?
    fun findByUserId(userId: UUID): List<Measurement>
    fun save(measurement: Measurement): Measurement
}
