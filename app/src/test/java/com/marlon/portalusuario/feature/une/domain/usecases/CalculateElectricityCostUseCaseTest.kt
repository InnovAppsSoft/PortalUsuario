package com.marlon.portalusuario.feature.une.domain.usecases

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculateElectricityCostUseCaseTest {

    private val useCase = CalculateElectricityCostUseCase()

    @Test
    fun `success with valid readings`() {
        val result = useCase("100", "150")

        assertTrue(result is ElectricityCostResult.Success)
        val success = result as ElectricityCostResult.Success
        assertEquals(50.0, success.consumption, 0.001)
        assertTrue(success.totalToPay > 0)
    }

    @Test
    fun `success with zero consumption`() {
        val result = useCase("200", "200")

        assertTrue(result is ElectricityCostResult.Error)
        assertEquals(
            "Lectura anterior y Lectura Actual no deben ser iguales",
            (result as ElectricityCostResult.Error).message,
        )
    }

    @Test
    fun `error when previous reading is invalid`() {
        val result = useCase("abc", "150")

        assertTrue(result is ElectricityCostResult.Error)
        assertTrue((result as ElectricityCostResult.Error).message.contains("Lectura Anterior"))
    }

    @Test
    fun `error when current reading is invalid`() {
        val result = useCase("100", "xyz")

        assertTrue(result is ElectricityCostResult.Error)
        assertTrue((result as ElectricityCostResult.Error).message.contains("Lectura Actual"))
    }

    @Test
    fun `error when both readings are invalid`() {
        val result = useCase("abc", "xyz")

        assertTrue(result is ElectricityCostResult.Error)
        val error = result as ElectricityCostResult.Error
        assertTrue(error.message.contains("Lectura Anterior"))
        assertTrue(error.message.contains("Lectura Actual"))
    }

    @Test
    fun `error when current is less than previous`() {
        val result = useCase("200", "100")

        assertTrue(result is ElectricityCostResult.Error)
        assertEquals(
            "La lectura Anterior no puede ser mayor a la Actual",
            (result as ElectricityCostResult.Error).message,
        )
    }

    @Test
    fun `error when current reading is negative`() {
        val result = useCase("100", "-50")

        assertTrue(result is ElectricityCostResult.Error)
        assertEquals(
            "Lectura Actual: Valor inválido",
            (result as ElectricityCostResult.Error).message,
        )
    }

    @Test
    fun `error when previous reading is negative`() {
        val result = useCase("-100", "50")

        assertTrue(result is ElectricityCostResult.Error)
        assertEquals(
            "Lectura Anterior: Valor inválido",
            (result as ElectricityCostResult.Error).message,
        )
    }

    @Test
    fun `error on empty previous reading`() {
        val result = useCase("", "150")

        assertTrue(result is ElectricityCostResult.Error)
        assertTrue((result as ElectricityCostResult.Error).message.contains("Lectura Anterior"))
    }

    @Test
    fun `error on empty current reading`() {
        val result = useCase("100", "")

        assertTrue(result is ElectricityCostResult.Error)
        assertTrue((result as ElectricityCostResult.Error).message.contains("Lectura Actual"))
    }

    @Test
    fun `success with decimal readings`() {
        val result = useCase("100.5", "150.7")

        assertTrue(result is ElectricityCostResult.Success)
        val success = result as ElectricityCostResult.Success
        assertEquals(50.2, success.consumption, 0.001)
    }

    @Test
    fun `success with trimmed whitespace`() {
        val result = useCase("  100  ", "  150  ")

        assertTrue(result is ElectricityCostResult.Success)
        val success = result as ElectricityCostResult.Success
        assertEquals(50.0, success.consumption, 0.001)
    }

    @Test
    fun `totalToPay is positive for valid consumption`() {
        val result = useCase("0", "100")

        assertTrue(result is ElectricityCostResult.Success)
        val success = result as ElectricityCostResult.Success
        assertEquals(100.0, success.consumption, 0.001)
        assertTrue("Total to pay should be positive", success.totalToPay > 0)
    }
}
