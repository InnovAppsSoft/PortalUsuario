package com.marlon.portalusuario.data.mappers

import com.marlon.portalusuario.data.entity.ClientProfile as Entity
import io.github.suitetecsa.sdk.nauta.model.User as Api

class ClientProfileApiToEntityMapper : Mapper<Api, Entity> {
    override fun map(from: Api) = Entity(
        email = from.client.email,
        name = from.client.name,
        mailNotifications = from.client.mailNotifications == "true",
        mobileNotifications = from.client.mobileNotifications == "true",
        phoneNumber = from.client.phoneNumber,
        portalUser = from.client.portalUser,
        lastUpdate = from.lastUpdate
    )
}
