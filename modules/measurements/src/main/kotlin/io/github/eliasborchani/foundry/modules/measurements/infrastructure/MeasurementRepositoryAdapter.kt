package io.github.eliasborchani.foundry.modules.measurements.infrastructure

import io.github.eliasborchani.foundry.modules.measurements.application.MeasurementRepository
import io.github.eliasborchani.foundry.modules.measurements.domain.MeasurementEntity
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class MeasurementRepositoryAdapter(
    private val jpa: MeasurementJpaRepository,
) : MeasurementRepository {

    override fun findById(id: UUID): MeasurementEntity? = jpa.findById(id).orElse(null)

    override fun findByUserId(userId: UUID): List<MeasurementEntity> = jpa.findAllByUserId(userId)

    override fun save(measurement: MeasurementEntity): MeasurementEntity = jpa.save(measurement)
}
