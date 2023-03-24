package com.marlon.portalusuario.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "user")
public class User implements Serializable {
    //
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String username;
    private String password;
    private String nautaEmailPassword;
    private String ATTRIBUTE_UUID;
    private String CSRFHW;
    private int accountNavegationType;
    private String leftTime;
    private String accountCredit;
    private String accountState;
    private long lastConnectionDateTime;
    // added 25-3-2022
    private String blockDate;
    private String delDate;
    private String accountType;
    private String serviceType;
    private String emailAccount;
    @Ignore
    private static User myUser;
    //
    @Ignore
    public User() {
        username = "prp";
    }

    public User(final String username, final String password, final String nautaEmailPassword, final String ATTRIBUTE_UUID, final String CSRFHW, final int accountNavegationType, final String leftTime, final String accountCredit, final String accountState, final long lastConnectionDateTime, final String blockDate, final String delDate, final String accountType, final String serviceType, final String emailAccount) {
        this.username = username;
        this.password = password;
        this.nautaEmailPassword = nautaEmailPassword;
        this.ATTRIBUTE_UUID = ATTRIBUTE_UUID;
        this.CSRFHW = CSRFHW;
        this.accountNavegationType = accountNavegationType;
        this.leftTime = leftTime;
        this.accountCredit = accountCredit;
        this.accountState = accountState;
        this.lastConnectionDateTime = lastConnectionDateTime;
        this.blockDate = blockDate;
        this.delDate = delDate;
        this.accountType = accountType;
        this.serviceType = serviceType;
        this.emailAccount = emailAccount;
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getFullUsername() {
        String mail = getAccountNavegationType() == 0 ? "@nauta.com.cu" : "@nauta.co.cu";
        return username + mail;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getATTRIBUTE_UUID()
    {
        return ATTRIBUTE_UUID;
    }

    public void setATTRIBUTE_UUID(String ATTRIBUTE_UUID)
    {
        this.ATTRIBUTE_UUID = ATTRIBUTE_UUID;
    }

    public String getCSRFHW()
    {
        return CSRFHW;
    }

    public void setCSRFHW(String CSRFHW)
    {
        this.CSRFHW = CSRFHW;
    }

    public String getLeftTime()
    {
        return leftTime;
    }

    public void setLeftTime(String leftTime)
    {
        this.leftTime = leftTime;
    }

    public String getAccountCredit()
    {
        return accountCredit;
    }

    public void setAccountCredit(String accountCredit)
    {
        this.accountCredit = accountCredit;
    }

    public String getAccountState()
    {
        return accountState;
    }

    public void setAccountState(String accountState) {
        this.accountState = accountState;
    }

    public int getAccountNavegationType() {
        return this.accountNavegationType;
    }

    public void setAccountNavegationType(final int accountNavegationType) {
        this.accountNavegationType = accountNavegationType;
    }

    public String getNautaEmailPassword() {
        return this.nautaEmailPassword;
    }

    public void setNautaEmailPassword(final String nautaEmailPassword) {
        this.nautaEmailPassword = nautaEmailPassword;
    }

    public long getLastConnectionDateTime() {
        return this.lastConnectionDateTime;
    }

    public void setLastConnectionDateTime(final long lastConnectionDateTime) {
        this.lastConnectionDateTime = lastConnectionDateTime;
    }

    public static User getUser(){
        if (myUser ==null){
            myUser = new User();
        }
        return myUser;
    }

    public void setUser(User user){
        myUser = user;
    }

    public String getBlockDate() {
        return this.blockDate;
    }

    public void setBlockDate(final String blockDate) {
        this.blockDate = blockDate;
    }

    public String getDelDate() {
        return this.delDate;
    }

    public void setDelDate(final String delDate) {
        this.delDate = delDate;
    }

    public String getAccountType() {
        return this.accountType;
    }

    public void setAccountType(final String accountType) {
        this.accountType = accountType;
    }

    public String getServiceType() {
        return this.serviceType;
    }

    public void setServiceType(final String serviceType) {
        this.serviceType = serviceType;
    }

    public String getEmailAccount() {
        return this.emailAccount;
    }

    public void setEmailAccount(final String emailAccount) {
        this.emailAccount = emailAccount;
    }

    @Override
    public String toString() {
        return username;
    }
}
