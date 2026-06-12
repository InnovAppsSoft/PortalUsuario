package com.marlon.portalusuario.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.marlon.portalusuario.data.entity.ClientProfile
import com.marlon.portalusuario.data.entity.Converters
import com.marlon.portalusuario.data.entity.MobileService
import com.marlon.portalusuario.data.entity.NavigationService
import com.marlon.portalusuario.model.User
import com.marlon.portalusuario.punotifications.PUNotification
import com.marlon.portalusuario.une.Une

@Database(
    entities = [
        ClientProfile::class,
        MobileService::class,
        NavigationService::class,
        User::class,
        Une::class,
        PUNotification::class,
    ],
    version = 2,
)
@TypeConverters(Converters::class)
abstract class ServicesDB : RoomDatabase() {
    abstract val servicesDao: ServicesDao

    companion object {
        val MIGRATION_1_2 =
            object : Migration(1, 2) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `user` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                            `username` TEXT,
                            `password` TEXT,
                            `nautaEmailPassword` TEXT,
                            `attributeUuid` TEXT,
                            `csrfhw` TEXT,
                            `accountNavegationType` INTEGER,
                            `leftTime` TEXT,
                            `accountCredit` TEXT,
                            `accountState` TEXT,
                            `lastConnectionDateTime` INTEGER,
                            `blockDate` TEXT,
                            `delDate` TEXT,
                            `accountType` TEXT,
                            `serviceType` TEXT,
                            `emailAccount` TEXT
                        )
                        """.trimIndent(),
                    )
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `une` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                            `date` INTEGER,
                            `lastRegister` REAL,
                            `currentRegister` REAL,
                            `totalConsumption` REAL,
                            `totalToPay` REAL
                        )
                        """.trimIndent(),
                    )
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `notifications` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                            `title` TEXT,
                            `text` TEXT,
                            `image` TEXT,
                            `date` INTEGER
                        )
                        """.trimIndent(),
                    )
                }
            }
    }
}
