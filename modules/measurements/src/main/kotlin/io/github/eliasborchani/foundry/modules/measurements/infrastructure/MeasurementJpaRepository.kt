package io.github.eliasborchani.foundry.modules.measurements.infrastructure

import io.github.eliasborchani.foundry.modules.measurements.domain.MeasurementEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface MeasurementJpaRepository : JpaRepository<MeasurementEntity, UUID> {
    fun findAllByUserId(userId: UUID): List<MeasurementEntity>
}
