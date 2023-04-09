package com.marlon.portalusuario.net;


import android.os.AsyncTask;

import cu.suitetecsa.sdk.nauta.domain.service.NautaClient;

public class RunTask extends AsyncTask<Void, Void, Void> {
    Communicator communicator;
    NautaClient session;
    String status;

    public RunTask(Communicator communicator2, NautaClient client) {
        this.communicator = communicator2;
        this.session = client;
        this.status = "";
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
