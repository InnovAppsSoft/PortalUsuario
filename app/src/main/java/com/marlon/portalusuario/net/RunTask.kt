package com.marlon.portalusuario.net

import android.os.AsyncTask

class RunTask(var communicator: Communicator) :
    AsyncTask<Void?, Void?, Void?>() {

    /* access modifiers changed from: protected */
    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg p0: Void?): Void? {
        this.communicator.communicate()
        return null
    }

    /* access modifiers changed from: protected */
    @Deprecated("Deprecated in Java", ReplaceWith("communicator.postCommunicate(error)"))
    public override fun onPostExecute(r1: Void?) {
        communicator.postCommunicate()
    }
}