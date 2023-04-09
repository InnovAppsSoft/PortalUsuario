package com.marlon.portalusuario.net;


import cu.suitetecsa.sdk.nauta.domain.service.NautaClient;

public interface Communicator {
    void Communicate();

    void Communicate(NautaClient client);

    void communicate();
}
