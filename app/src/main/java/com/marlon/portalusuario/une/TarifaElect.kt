package com.marlon.portalusuario.une

object TarifaElect {
    val rangosConsumo: List<RangoConsumo> =
        listOf(
            RangoConsumo(0.0, 100.0, 0.33),
            RangoConsumo(100.0, 150.0, 1.07),
            RangoConsumo(150.0, 200.0, 1.43),
            RangoConsumo(200.0, 250.0, 2.46),
            RangoConsumo(250.0, 300.0, 3.0),
            RangoConsumo(300.0, 350.0, 4.0),
            RangoConsumo(350.0, 400.0, 5.0),
            RangoConsumo(400.0, 450.0, 6.0),
            RangoConsumo(450.0, 500.0, 7.0),
            RangoConsumo(500.0, 600.0, 9.2),
            RangoConsumo(600.0, 700.0, 9.45),
            RangoConsumo(700.0, 1000.0, 9.85),
            RangoConsumo(1000.0, 1800.0, 10.8),
            RangoConsumo(1800.0, 2600.0, 11.8),
            RangoConsumo(2600.0, 3400.0, 12.9),
            RangoConsumo(3400.0, 4200.0, 13.95),
            RangoConsumo(4200.0, 5000.0, 15.0),
            RangoConsumo(5000.0, 9.99999999E8, 20.0),
        )

    fun calculateCost(consumptionKwh: Double): Double {
        var remaining = consumptionKwh
        var total = 0.0
        for (rango in rangosConsumo) {
            val rangeSize = rango.finRango - rango.inicioRango
            if (remaining > rangeSize) {
                total += rangeSize * rango.precioRango
                remaining -= rangeSize
            } else {
                total += remaining * rango.precioRango
                break
            }
        }
        return total
    }
}
