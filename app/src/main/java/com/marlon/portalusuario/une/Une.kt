package com.marlon.portalusuario.une

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.marlon.portalusuario.util.Util

@Entity(tableName = "une")
data class Une(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: Long = 0L,
    val lastRegister: Double = 0.0,
    val currentRegister: Double = 0.0,
    val totalConsumption: Double = 0.0,
    val totalToPay: Double = 0.0,
) : Comparable<Une> {
    fun dateToString(): String = Util.date2String(Util.long2Date(date))

    override fun compareTo(other: Une): Int = Util.long2Date(date).compareTo(Util.long2Date(other.date))
}
