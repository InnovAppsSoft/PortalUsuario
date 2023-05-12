package com.marlon.portalusuario.database.notifications;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.preference.PreferenceManager;

import com.marlon.portalusuario.PUNotifications.PUNotification;
import com.marlon.portalusuario.database.notifications.PunDAO;
import com.marlon.portalusuario.database.notifications.PunDataBase;

import java.util.List;

public class PunRepository {
    private final PunDAO punDao;
    private final PunDataBase database;
    private final LiveData<List<PUNotification>> allPUN;
    private static Application application;

    public PunRepository(Application application){
        PunRepository.application = application;
        database = PunDataBase.getInstance(application);
        punDao = database.DAO();
        allPUN = punDao.getAllPUNotification();
    }

    public void insertPUNotification(PUNotification pun){
        new InsertPUNotificationAsyncTask(punDao).execute(pun);
    }

    public void updatePUNotification(PUNotification pun){
        new UpdatePUNotificationAsyncTask(punDao).execute(pun);
    }

    public void deletePUNotification(PUNotification pun){
        new DeletePUNotificationAsyncTask(punDao).execute(pun);
    }

    public void deleteAllPUNotifications(){
        new DeleteAllPUNotificationsAsyncTask(punDao).execute();
    }

    public LiveData<List<PUNotification>> getAllPUN(){
        return allPUN;
    }

    public static class InsertPUNotificationAsyncTask extends AsyncTask<PUNotification, Void, Void> {
        private final PunDAO punDao;
        private SharedPreferences sharedPreferences;

        private InsertPUNotificationAsyncTask(PunDAO punDao){
            this.punDao = punDao;
        }

        @Override
        protected Void doInBackground(PUNotification... puns) {
            punDao.insertPUNotification(puns[0]);
            //
            //
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application.getApplicationContext());
            int count = sharedPreferences.getInt("notifications_count", 0);
            SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
            count++;
            prefsEditor.putInt("notifications_count", count);
            prefsEditor.apply();
            try{
                Toast.makeText(application.getApplicationContext(), "Nuevo mensaje de Portal Usuario", Toast.LENGTH_LONG).show();
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return null;
        }
    }

    public static class UpdatePUNotificationAsyncTask extends AsyncTask<PUNotification, Void, Void> {
        private final PunDAO punDao;

        private UpdatePUNotificationAsyncTask(PunDAO punDao){
            this.punDao = punDao;
        }

        @Override
        protected Void doInBackground(PUNotification... puns) {
            punDao.updatePUNotification(puns[0]);
            return null;
        }
    }

    public static class DeletePUNotificationAsyncTask extends AsyncTask<PUNotification, Void, Void> {
        private final PunDAO punDao;

        private DeletePUNotificationAsyncTask(PunDAO punDao){
            this.punDao = punDao;
        }

        @Override
        protected Void doInBackground(PUNotification... puns) {
            punDao.deletePUNotification(puns[0]);
            return null;
        }
    }

    public static class DeleteAllPUNotificationsAsyncTask extends AsyncTask<Void, Void, Void> {
        private final PunDAO punDao;

        private DeleteAllPUNotificationsAsyncTask(PunDAO punDao){
            this.punDao = punDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            punDao.deleteAllPUNotification();
            return null;
        }
    }
}
