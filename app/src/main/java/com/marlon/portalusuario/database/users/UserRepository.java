package com.marlon.portalusuario.database.users;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.marlon.portalusuario.model.User;

import java.util.List;

public class UserRepository {
    private UserDAO userDao;
    private UserDataBase database;
    private LiveData<List<User>> allUsers;
    private static Application application;

    public UserRepository(Application application){
        this.application = application;
        database = UserDataBase.getInstance(application);
        userDao = database.DAO();
        allUsers = userDao.getAllUser();
    }

    public void insertUser(User pun){
        new InsertUserAsyncTask(userDao).execute(pun);
    }

    public void updateUser(User pun){
        new UpdateUserAsyncTask(userDao).execute(pun);
    }

    public void deleteUser(User pun){
        new DeleteUserAsyncTask(userDao).execute(pun);
    }

    public void deleteAllUsers(){
        new DeleteAllUsersAsyncTask(userDao).execute();
    }

    public LiveData<List<User>> getAllUser(){
        return allUsers;
    }

    public static class InsertUserAsyncTask extends AsyncTask<User, Void, Void> {
        private UserDAO userDao;
        private SharedPreferences sharedPreferences;

        private InsertUserAsyncTask(UserDAO userDao){
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(User... user) {
            userDao.insertUser(user[0]);
            //
            return null;
        }
    }

    public static class UpdateUserAsyncTask extends AsyncTask<User, Void, Void> {
        private UserDAO userDao;

        private UpdateUserAsyncTask(UserDAO userDao){
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(User... user) {
            userDao.updateUser(user[0]);
            return null;
        }
    }

    public static class DeleteUserAsyncTask extends AsyncTask<User, Void, Void> {
        private UserDAO userDao;

        private DeleteUserAsyncTask(UserDAO userDao){
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(User... user) {
            userDao.deleteUser(user[0]);
            return null;
        }
    }

    public static class DeleteAllUsersAsyncTask extends AsyncTask<Void, Void, Void> {
        private UserDAO userDao;

        private DeleteAllUsersAsyncTask(UserDAO userDao){
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            userDao.deleteAllUser();
            return null;
        }
    }
}
