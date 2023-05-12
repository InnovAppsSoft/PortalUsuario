package com.marlon.portalusuario.database.users;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.marlon.portalusuario.model.User;

@Database(entities = {User.class}, version = 3, exportSchema = false)
public abstract class UserDataBase extends RoomDatabase {
    // singleton
    private static volatile UserDataBase instance;
    //
    public abstract UserDAO DAO();

    public static synchronized UserDataBase getInstance(Context context) {
        if (instance == null){
            synchronized (UserDataBase.class) {
                instance = Room.databaseBuilder(context,
                        UserDataBase.class, "users_db")
                        .addCallback(roomCallback)
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return instance;
    }

    private static final Callback roomCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };

}
