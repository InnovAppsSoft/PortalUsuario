package com.marlon.portalusuario.data.mappers

import com.marlon.portalusuario.data.entity.ClientProfile as Entity
import com.marlon.portalusuario.domain.model.ClientProfile as Domain

class ClientProfileEntityToDomainMapper : Mapper<Entity, Domain> {
    override fun map(from: Entity) = Domain(
        email = from.email,
        name = from.name,
        mailNotifications = from.mailNotifications,
        mobileNotifications = from.mobileNotifications,
        phoneNumber = from.phoneNumber,
        portalUser = from.portalUser,
        lastUpdate = from.lastUpdate
    )
}
