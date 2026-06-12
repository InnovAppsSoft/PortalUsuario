package com.marlon.portalusuario.database.users

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.marlon.portalusuario.model.User

@Database(entities = [User::class], version = 3, exportSchema = false)
abstract class UserDataBase : RoomDatabase() {
    abstract fun dao(): UserDAO

    companion object {
        @Volatile
        private var instance: UserDataBase? = null

        fun getInstance(context: Context): UserDataBase {
            return instance ?: synchronized(this) {
                instance ?: Room
                    .databaseBuilder(
                        context.applicationContext,
                        UserDataBase::class.java,
                        "users_db",
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
