package com.marlon.portalusuario.punotifications

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.marlon.portalusuario.util.Util

@Entity(tableName = "notifications")
data class PUNotification(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var title: String = "",
    var text: String = "",
    var image: String = "",
    var date: Long = 0L,
) : Comparable<PUNotification> {
    constructor(
        title: String,
        text: String,
        image: String,
        date: Long,
    ) : this(id = 0, title = title, text = text, image = image, date = date)

    fun dateToString(): String = Util.date2String(Util.long2Date(date))

    override fun compareTo(other: PUNotification): Int = Util.long2Date(other.date).compareTo(Util.long2Date(date))
}
