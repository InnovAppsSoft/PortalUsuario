package com.marlon.portalusuario.permisos

import org.junit.Assert.assertEquals
import org.junit.Test

class PermissionViewModelTest {
    private val viewModel = PermissionViewModel()

    @Test
    fun `currentStep starts at 0`() {
        assertEquals(0, viewModel.currentStep)
    }

    @Test
    fun `nextStep increments currentStep`() {
        viewModel.nextStep()
        assertEquals(1, viewModel.currentStep)
        viewModel.nextStep()
        assertEquals(2, viewModel.currentStep)
    }

    @Test
    fun `onOverlayPermissionResult with true advances to next step`() {
        viewModel.onOverlayPermissionResult(true)
        assertEquals(1, viewModel.currentStep)
    }

    @Test
    fun `onOverlayPermissionResult with false does not advance`() {
        viewModel.onOverlayPermissionResult(false)
        assertEquals(0, viewModel.currentStep)
    }
}
