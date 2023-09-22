package cu.suitetecsa.nautanav.data.network

import androidx.room.Database
import androidx.room.RoomDatabase
import cu.suitetecsa.nautanav.data.UserDao
import cu.suitetecsa.nautanav.data.entities.User

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class UserDb : RoomDatabase() {
    abstract val userDao: UserDao
}