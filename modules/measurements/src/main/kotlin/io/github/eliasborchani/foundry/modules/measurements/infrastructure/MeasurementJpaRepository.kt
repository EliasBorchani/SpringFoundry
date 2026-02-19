package io.github.eliasborchani.foundry.modules.measurements.infrastructure

import io.github.eliasborchani.foundry.modules.measurements.domain.Measurement
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface MeasurementJpaRepository : JpaRepository<Measurement, UUID> {
    fun findAllByUserId(userId: UUID): List<Measurement>
}
