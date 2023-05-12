package com.marlon.portalusuario.database.une;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.marlon.portalusuario.une.Une;


@Database(entities = {Une.class}, version = 1, exportSchema = false)
public abstract class UneDataBase extends RoomDatabase {
    // singleton
    private static volatile UneDataBase instance;
    //
    public abstract UneDAO DAO();

    public static synchronized UneDataBase getInstance(Context context) {
        if (instance == null){
            synchronized (UneDataBase.class) {
                instance = Room.databaseBuilder(context,
                        UneDataBase.class, "une_db")
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
