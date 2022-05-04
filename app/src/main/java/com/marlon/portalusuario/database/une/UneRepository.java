package com.marlon.portalusuario.database.une;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.preference.PreferenceManager;

import com.marlon.portalusuario.une.Une;

import java.util.List;

public class UneRepository {
    private UneDAO uneDao;
    private UneDataBase database;
    private LiveData<List<Une>> allUnes;
    private static Application application;

    public UneRepository(Application application){
        this.application = application;
        database = UneDataBase.getInstance(application);
        uneDao = database.DAO();
        allUnes = uneDao.getAllUne();
    }

    public void insertUne(Une une){
        new InsertUneAsyncTask(uneDao).execute(une);
    }

    public void updateUne(Une une){
        new UpdateUneAsyncTask(uneDao).execute(une);
    }

    public void deleteUne(Une une){
        new DeleteUneAsyncTask(uneDao).execute(une);
    }

    public void deleteAllUnes(){
        new DeleteAllUnesAsyncTask(uneDao).execute();
    }

    public LiveData<List<Une>> getAllUne(){
        return allUnes;
    }

    public static class InsertUneAsyncTask extends AsyncTask<Une, Void, Void> {
        private UneDAO uneDao;
        private SharedPreferences sharedPreferences;

        private InsertUneAsyncTask(UneDAO uneDao){
            this.uneDao = uneDao;
        }

        @Override
        protected Void doInBackground(Une... user) {
            uneDao.insertUne(user[0]);
            //
            return null;
        }
    }

    public static class UpdateUneAsyncTask extends AsyncTask<Une, Void, Void> {
        private UneDAO uneDao;

        private UpdateUneAsyncTask(UneDAO uneDao){
            this.uneDao = uneDao;
        }

        @Override
        protected Void doInBackground(Une... user) {
            uneDao.updateUne(user[0]);
            return null;
        }
    }

    public static class DeleteUneAsyncTask extends AsyncTask<Une, Void, Void> {
        private UneDAO uneDao;

        private DeleteUneAsyncTask(UneDAO uneDao){
            this.uneDao = uneDao;
        }

        @Override
        protected Void doInBackground(Une... user) {
            uneDao.deleteUne(user[0]);
            return null;
        }
    }

    public static class DeleteAllUnesAsyncTask extends AsyncTask<Void, Void, Void> {
        private UneDAO uneDao;

        private DeleteAllUnesAsyncTask(UneDAO uneDao){
            this.uneDao = uneDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            uneDao.deleteAllUne();
            return null;
        }
    }
}
