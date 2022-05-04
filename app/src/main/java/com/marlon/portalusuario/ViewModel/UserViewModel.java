package com.marlon.portalusuario.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.marlon.portalusuario.User;
import com.marlon.portalusuario.database.users.UserRepository;

import java.util.List;

public class UserViewModel extends AndroidViewModel {
    private UserRepository userRepository;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
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
}
