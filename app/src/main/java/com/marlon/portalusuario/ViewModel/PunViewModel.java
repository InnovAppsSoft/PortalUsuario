package com.marlon.portalusuario.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.marlon.portalusuario.model.User;
import com.marlon.portalusuario.database.une.UneRepository;
import com.marlon.portalusuario.database.users.UserRepository;
import com.marlon.portalusuario.une.Une;

import java.util.List;

public class PunViewModel extends AndroidViewModel {
    private UserRepository userRepository;
    private UneRepository uneRepository;

    public PunViewModel(@NonNull Application application) {
        super(application);

        userRepository = new UserRepository(application);
        uneRepository = new UneRepository(application);
    }


    // USERS
    public void insertUser(User user){
        userRepository.insertUser(user);
    }

    public void updateUser(User user){
        userRepository.updateUser(user);
    }

    public void deleteUser(User user){
        userRepository.deleteUser(user);
    }

    public void deleteAllUsers(){
        userRepository.deleteAllUsers();
    }

    public LiveData<List<User>> getAllUsers(){
        return userRepository.getAllUser();
    }

    // UNE
    public void insertUne(Une une){
        uneRepository.insertUne(une);
    }

    public void updateUne(Une une){
        uneRepository.updateUne(une);
    }

    public void deleteUne(Une une){
        uneRepository.deleteUne(une);
    }

    public void deleteAllUnes(){
        uneRepository.deleteAllUnes();
    }

    public LiveData<List<Une>> getAllUnes(){
        return uneRepository.getAllUne();
    }
}
