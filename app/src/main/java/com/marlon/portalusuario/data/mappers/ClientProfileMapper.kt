package com.marlon.portalusuario.data.mappers

import com.marlon.portalusuario.data.entity.ClientProfile as Entity
import com.marlon.portalusuario.domain.model.ClientProfile as Domain
import io.github.suitetecsa.sdk.nauta.model.User as Api

class ClientProfileMapper : Mapper<Domain, Entity, Api> {
    override fun Api.toEntity() = Entity(
        email = client.email,
        name = client.name,
        mailNotifications = client.mailNotifications == "true",
        mobileNotifications = client.mobileNotifications == "true",
        phoneNumber = client.phoneNumber,
        portalUser = client.portalUser,
        lastUpdate = lastUpdate
    )
    override fun Entity.toDomain() = Domain(
        email = email,
        name = name,
        mailNotifications = mailNotifications,
        mobileNotifications = mobileNotifications,
        phoneNumber = phoneNumber,
        portalUser = portalUser,
        lastUpdate = lastUpdate
    )
}
