package com.marlon.portalusuario.data.mappers

import com.marlon.portalusuario.data.entity.NavigationService as Entity
import com.marlon.portalusuario.domain.model.NavigationService as Domain

class NavServEntityToDomainMapper : Mapper<Entity, Domain> {
    override fun map(from: Entity) = Domain(
        from.id,
        from.bonusToEnjoy,
        from.accessAccount,
        from.status,
        from.lockDate,
        from.deletionDate,
        from.saleDate,
        from.bonusHours,
        from.currency,
        from.balance,
        from.accessType,
        from.productType
    )
}
