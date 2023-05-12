package com.marlon.portalusuario.database.notifications;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.marlon.portalusuario.PUNotifications.PUNotification;

import java.util.Calendar;

@Database(entities = {PUNotification.class}, version = 1, exportSchema = false)
public abstract class PunDataBase extends RoomDatabase {
    // singleton
    private static volatile PunDataBase instance;
    //
    public abstract PunDAO DAO();

    public static synchronized PunDataBase getInstance(Context context) {
        if (instance == null){
            synchronized (PunDataBase.class) {
                instance = Room.databaseBuilder(context,
                        PunDataBase.class, "notifications_db")
                        .addCallback(roomCallback)
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return instance;
    }

    private static final RoomDatabase.Callback roomCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            //new PopulateDBAsyncTask(instance).execute();
        }
    };

    public static class PopulateDBAsyncTask extends AsyncTask<Void, Void, Void>{
        private final PunDAO punDao;

        private PopulateDBAsyncTask(PunDataBase db){
            punDao = db.DAO();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            punDao.insertPUNotification(new PUNotification("Prueba", "Prueba", "", Calendar.getInstance().getTimeInMillis()));
            return null;
        }
    }

}
