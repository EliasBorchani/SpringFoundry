package io.github.eliasborchani.foundry.modules.measurements.application

import io.github.eliasborchani.foundry.modules.measurements.application.dto.MeasurementDto
import io.github.eliasborchani.foundry.modules.measurements.domain.Measurement
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class MeasurementService(private val measurementRepository: MeasurementRepository) : MeasurementPort {

    override fun findById(id: UUID): MeasurementDto? =
        measurementRepository.findById(id)?.let(MeasurementDto::from)

    override fun findByUserId(userId: UUID): List<MeasurementDto> =
        measurementRepository.findByUserId(userId).map(MeasurementDto::from)

    @Transactional
    override fun record(userId: UUID, type: String, value: Double, unit: String): MeasurementDto {
        val measurement = measurementRepository.save(
            Measurement(userId = userId, type = type, value = value, unit = unit),
        )
        return MeasurementDto.from(measurement)
    }
}
