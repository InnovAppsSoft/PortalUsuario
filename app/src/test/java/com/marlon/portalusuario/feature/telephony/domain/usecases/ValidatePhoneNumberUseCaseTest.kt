package com.marlon.portalusuario.feature.telephony.domain.usecases

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidatePhoneNumberUseCaseTest {
    private val useCase = ValidatePhoneNumberUseCase()

    @Test
    fun `valid phone number with 8 digits`() {
        assertTrue(useCase("12345678"))
    }

    @Test
    fun `valid phone number with more than 8 digits`() {
        assertTrue(useCase("123456789"))
    }

    @Test
    fun `invalid phone number with less than 8 digits`() {
        assertFalse(useCase("1234567"))
    }

    @Test
    fun `invalid phone number with letters`() {
        assertFalse(useCase("12345abc"))
    }

    @Test
    fun `invalid empty phone number`() {
        assertFalse(useCase(""))
    }

    @Test
    fun `invalid phone number with special characters`() {
        assertFalse(useCase("1234-678"))
    }

    @Test
    fun `valid phone number with leading zeros`() {
        assertTrue(useCase("01234567"))
    }
}
