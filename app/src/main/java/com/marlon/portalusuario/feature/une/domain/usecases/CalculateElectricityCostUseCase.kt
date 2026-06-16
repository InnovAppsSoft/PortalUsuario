package com.marlon.portalusuario.feature.une.domain.usecases

import com.marlon.portalusuario.une.TarifaElect
import com.marlon.portalusuario.util.Util
import javax.inject.Inject

sealed class ElectricityCostResult {
    data class Success(
        val consumption: Double,
        val totalToPay: Double,
    ) : ElectricityCostResult()

    data class Error(
        val message: String,
    ) : ElectricityCostResult()
}

class CalculateElectricityCostUseCase
    @Inject
    constructor() {
        operator fun invoke(
            previousReading: String,
            currentReading: String,
        ): ElectricityCostResult {
            val previousText = previousReading.trim()
            val currentText = currentReading.trim()

            val errors = mutableListOf<String>()
            val previous = previousText.toDoubleOrNull()
            if (previous == null) errors.add("Lectura Anterior: Valor inválido")

            val current = currentText.toDoubleOrNull()
            if (current == null) errors.add("Lectura Actual: Valor inválido")

            if (errors.isNotEmpty()) {
                return ElectricityCostResult.Error(errors.joinToString("\n"))
            }

            if (current!! < 0) {
                return ElectricityCostResult.Error("Lectura Actual: Valor inválido")
            }
            if (previous!! < 0) {
                return ElectricityCostResult.Error("Lectura Anterior: Valor inválido")
            }
            if (current < previous) {
                return ElectricityCostResult.Error(
                    "La lectura Anterior no puede ser mayor a la Actual",
                )
            }
            if (current == previous) {
                return ElectricityCostResult.Error(
                    "Lectura anterior y Lectura Actual no deben ser iguales",
                )
            }

            val consumption = Util.roundDouble(current - previous)
            val totalToPay = Util.roundDouble(TarifaElect.calculateCost(consumption))

            return ElectricityCostResult.Success(
                consumption = consumption,
                totalToPay = totalToPay,
            )
        }
    }
