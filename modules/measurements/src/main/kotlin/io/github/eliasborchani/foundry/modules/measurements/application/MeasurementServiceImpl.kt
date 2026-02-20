package io.github.eliasborchani.foundry.modules.measurements.application

import io.github.eliasborchani.foundry.modules.measurements.application.dto.Measurement
import io.github.eliasborchani.foundry.modules.measurements.domain.MeasurementEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class MeasurementServiceImpl(private val measurementRepository: MeasurementRepository) : MeasurementService {

    override fun findById(id: UUID): Measurement? =
        measurementRepository.findById(id)?.let(Measurement::from)

    override fun findByUserId(userId: UUID): List<Measurement> =
        measurementRepository.findByUserId(userId).map(Measurement::from)

    @Transactional
    override fun record(userId: UUID, type: String, value: Double, unit: String): Measurement {
        val measurement = measurementRepository.save(
            MeasurementEntity(userId = userId, type = type, value = value, unit = unit),
        )
        return Measurement.from(measurement)
    }
}
