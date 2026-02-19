package io.github.eliasborchani.foundry.modules.measurements.application

import io.github.eliasborchani.foundry.modules.measurements.application.dto.MeasurementDto
import java.util.UUID

/**
 * Inbound port: the public API of the measurements module, exposed to apps and controllers.
 * Implemented by [MeasurementService].
 */
interface MeasurementPort {
    fun findById(id: UUID): MeasurementDto?
    fun findByUserId(userId: UUID): List<MeasurementDto>
    fun record(userId: UUID, type: String, value: Double, unit: String): MeasurementDto
}
