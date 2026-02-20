package io.github.eliasborchani.foundry.modules.measurements.application

import io.github.eliasborchani.foundry.modules.measurements.application.dto.Measurement
import java.util.UUID

/**
 * Inbound port: the public API of the measurements module, exposed to apps and controllers.
 * Implemented by [MeasurementServiceImpl].
 */
interface MeasurementService {
    fun findById(id: UUID): Measurement?
    fun findByUserId(userId: UUID): List<Measurement>
    fun record(userId: UUID, type: String, value: Double, unit: String): Measurement
}
