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
import com.marlon.portalusuario.domain.model.User
import com.marlon.portalusuario.feature.notifications.PUNotification
import com.marlon.portalusuario.feature.une.Une

@Database(
    entities = [
        ClientProfile::class,
        MobileService::class,
        NavigationService::class,
        User::class,
        Une::class,
        PUNotification::class,
    ],
    version = 3,
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
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `username` TEXT NOT NULL,
                            `password` TEXT NOT NULL,
                            `nautaEmailPassword` TEXT NOT NULL,
                            `attributeUuid` TEXT NOT NULL,
                            `csrfhw` TEXT NOT NULL,
                            `accountNavegationType` INTEGER NOT NULL,
                            `leftTime` TEXT NOT NULL,
                            `accountCredit` TEXT NOT NULL,
                            `accountState` TEXT NOT NULL,
                            `lastConnectionDateTime` INTEGER NOT NULL,
                            `blockDate` TEXT NOT NULL,
                            `delDate` TEXT NOT NULL,
                            `accountType` TEXT NOT NULL,
                            `serviceType` TEXT NOT NULL,
                            `emailAccount` TEXT NOT NULL
                        )
                        """.trimIndent(),
                    )
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `une` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `date` INTEGER NOT NULL,
                            `lastRegister` REAL NOT NULL,
                            `currentRegister` REAL NOT NULL,
                            `totalConsumption` REAL NOT NULL,
                            `totalToPay` REAL NOT NULL
                        )
                        """.trimIndent(),
                    )
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `notifications` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `title` TEXT NOT NULL,
                            `text` TEXT NOT NULL,
                            `image` TEXT NOT NULL,
                            `date` INTEGER NOT NULL
                        )
                        """.trimIndent(),
                    )
                }
            }

        val MIGRATION_2_3 =
            object : Migration(2, 3) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("CREATE TABLE IF NOT EXISTS `user_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `username` TEXT NOT NULL, `password` TEXT NOT NULL, `nautaEmailPassword` TEXT NOT NULL, `attributeUuid` TEXT NOT NULL, `csrfhw` TEXT NOT NULL, `accountNavegationType` INTEGER NOT NULL, `leftTime` TEXT NOT NULL, `accountCredit` TEXT NOT NULL, `accountState` TEXT NOT NULL, `lastConnectionDateTime` INTEGER NOT NULL, `blockDate` TEXT NOT NULL, `delDate` TEXT NOT NULL, `accountType` TEXT NOT NULL, `serviceType` TEXT NOT NULL, `emailAccount` TEXT NOT NULL)")
                    db.execSQL("INSERT INTO `user_new` SELECT * FROM `user`")
                    db.execSQL("DROP TABLE `user`")
                    db.execSQL("ALTER TABLE `user_new` RENAME TO `user`")

                    db.execSQL("CREATE TABLE IF NOT EXISTS `une_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` INTEGER NOT NULL, `lastRegister` REAL NOT NULL, `currentRegister` REAL NOT NULL, `totalConsumption` REAL NOT NULL, `totalToPay` REAL NOT NULL)")
                    db.execSQL("INSERT INTO `une_new` SELECT * FROM `une`")
                    db.execSQL("DROP TABLE `une`")
                    db.execSQL("ALTER TABLE `une_new` RENAME TO `une`")

                    db.execSQL("CREATE TABLE IF NOT EXISTS `notifications_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `text` TEXT NOT NULL, `image` TEXT NOT NULL, `date` INTEGER NOT NULL)")
                    db.execSQL("INSERT INTO `notifications_new` SELECT * FROM `notifications`")
                    db.execSQL("DROP TABLE `notifications`")
                    db.execSQL("ALTER TABLE `notifications_new` RENAME TO `notifications`")
                }
            }
    }
}
