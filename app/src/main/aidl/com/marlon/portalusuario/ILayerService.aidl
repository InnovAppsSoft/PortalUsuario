// ILayerService.aidl
package com.marlon.portalusuario;

interface ILayerService {

    void restart();

    void stop();

    void startSnapshot(long previewBytes);
}
