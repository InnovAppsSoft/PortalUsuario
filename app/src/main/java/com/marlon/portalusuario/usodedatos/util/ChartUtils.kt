package com.marlon.portalusuario.usodedatos.util

import com.github.mikephil.charting.data.Entry
import com.marlon.portalusuario.usodedatos.database.DailyConsumption

class ChartUtils {
    companion object{

        fun getDailyMobileConsumptionEntries(listConsumption: List<DailyConsumption>): List<Entry> {
            var entries : MutableList<Entry> = mutableListOf()
            var xEntry = 0.0F

            for(dailyConsumption in listConsumption.asReversed()){
                var entry = Entry(xEntry,dailyConsumption.mobile.toFloat())
                xEntry += 1
                entries.add(entry)
            }
            return entries
        }

        fun getDailyWifiConsumptionEntries(listConsumption: List<DailyConsumption>): List<Entry> {
            var entries : MutableList<Entry> = mutableListOf()
            var xEntry = 0.0F

            for(dailyConsumption in listConsumption.asReversed()){
                var entry = Entry(xEntry,dailyConsumption.wifi.toFloat())
                xEntry += 1
                entries.add(entry)
            }
            return entries
        }
    }
}