package io.github.eliasborchani.foundry.modules.measurements.application

import io.github.eliasborchani.foundry.modules.measurements.domain.MeasurementEntity
import java.util.UUID

/**
 * Outbound port: data-access contract used by [MeasurementServiceImpl].
 * Implemented in the infrastructure layer by [MeasurementRepositoryAdapter].
 */
interface MeasurementRepository {
    fun findById(id: UUID): MeasurementEntity?
    fun findByUserId(userId: UUID): List<MeasurementEntity>
    fun save(measurement: MeasurementEntity): MeasurementEntity
}
