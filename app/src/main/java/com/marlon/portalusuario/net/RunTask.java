package com.marlon.portalusuario.net;


import android.os.AsyncTask;
import cu.marilasoft.selibrary.UserPortal;

public class RunTask extends AsyncTask<Void, Void, Void> {
    Communicator communicator;
    UserPortal session;

    public RunTask(Communicator communicator2, UserPortal userPortal) {
        this.communicator = communicator2;
        this.session = userPortal;
    }

    /* access modifiers changed from: protected */
    public Void doInBackground(Void... voidArr) {
        this.communicator.Communicate();
        this.communicator.Communicate(this.session);
        return null;
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(Void r1) {
        this.communicator.communicate();
    }
}
