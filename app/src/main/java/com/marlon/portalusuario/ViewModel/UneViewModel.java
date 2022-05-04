package com.marlon.portalusuario.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.marlon.portalusuario.PUNotifications.PUNotification;
import com.marlon.portalusuario.User;
import com.marlon.portalusuario.database.notifications.PunRepository;
import com.marlon.portalusuario.database.une.UneRepository;
import com.marlon.portalusuario.database.users.UserRepository;
import com.marlon.portalusuario.une.Une;

import java.util.List;

public class UneViewModel extends AndroidViewModel {
    private UneRepository uneRepository;

    public UneViewModel(@NonNull Application application) {
        super(application);
        uneRepository = new UneRepository(application);
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
