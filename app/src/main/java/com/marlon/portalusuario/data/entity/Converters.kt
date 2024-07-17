package com.marlon.portalusuario.data.entity

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.marlon.portalusuario.domain.model.MobileBonus
import com.marlon.portalusuario.domain.model.MobilePlan

class Converters {

    @TypeConverter
    fun fromMobilePlanList(value: List<MobilePlan>): String {
        val gson = Gson()
        val type = object : TypeToken<List<MobilePlan>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toMobilePlanList(value: String): List<MobilePlan> {
        val gson = Gson()
        val type = object : TypeToken<List<MobilePlan>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromMobileBonusList(value: List<MobileBonus>): String {
        val gson = Gson()
        val type = object : TypeToken<List<MobileBonus>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toMobileBonusList(value: String): List<MobileBonus> {
        val gson = Gson()
        val type = object : TypeToken<List<MobileBonus>>() {}.type
        return gson.fromJson(value, type)
    }
}
