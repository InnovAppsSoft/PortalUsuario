package com.marlon.portalusuario.database.une

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.marlon.portalusuario.une.Une

@Database(entities = [Une::class], version = 1, exportSchema = false)
abstract class UneDataBase : RoomDatabase() {
    abstract fun dao(): UneDAO

    companion object {
        @Volatile
        private var instance: UneDataBase? = null

        fun getInstance(context: Context): UneDataBase {
            return instance ?: synchronized(this) {
                instance ?: Room
                    .databaseBuilder(
                        context.applicationContext,
                        UneDataBase::class.java,
                        "une_db",
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
