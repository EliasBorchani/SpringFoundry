package io.github.eliasborchani.foundry.modules.measurements.infrastructure

import io.github.eliasborchani.foundry.modules.measurements.application.MeasurementRepository
import io.github.eliasborchani.foundry.modules.measurements.domain.Measurement
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class MeasurementRepositoryAdapter(
    private val jpa: MeasurementJpaRepository,
) : MeasurementRepository {

    override fun findById(id: UUID): Measurement? = jpa.findById(id).orElse(null)

    override fun findByUserId(userId: UUID): List<Measurement> = jpa.findAllByUserId(userId)

    override fun save(measurement: Measurement): Measurement = jpa.save(measurement)
}
