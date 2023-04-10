package com.marlon.portalusuario.net

import cu.suitetecsa.sdk.nauta.domain.service.NautaClient

interface Communicator {
    fun communicate()
    fun postCommunicate()
}