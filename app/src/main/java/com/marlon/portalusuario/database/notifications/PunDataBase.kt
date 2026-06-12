package com.marlon.portalusuario.database.notifications

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.marlon.portalusuario.punotifications.PUNotification

@Database(entities = [PUNotification::class], version = 1, exportSchema = false)
abstract class PunDataBase : RoomDatabase() {
    abstract fun dao(): PunDAO

    companion object {
        @Volatile
        private var instance: PunDataBase? = null

        fun getInstance(context: Context): PunDataBase {
            return instance ?: synchronized(this) {
                instance ?: Room
                    .databaseBuilder(
                        context.applicationContext,
                        PunDataBase::class.java,
                        "notifications_db",
                    )
                    .addCallback(roomCallback)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
        }

        private val roomCallback: Callback =
            object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                }
            }
    }
}
