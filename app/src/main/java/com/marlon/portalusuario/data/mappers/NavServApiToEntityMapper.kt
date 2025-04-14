package com.marlon.portalusuario.data.mappers

import com.marlon.portalusuario.data.entity.NavigationService as Entity
import io.github.suitetecsa.sdk.nauta.model.NavService as Api

class NavServApiToEntityMapper : Mapper<Api, Entity> {
    override fun map(from: Api) = Entity(
        from.profile.id,
        from.profile.bonusToEnjoy,
        from.profile.accessAccount,
        from.profile.status,
        from.profile.lockDate,
        from.profile.deletionDate,
        from.profile.saleDate,
        from.profile.bonusHours,
        from.profile.currency,
        from.profile.balance,
        from.profile.accessType,
        from.productType
    )
}
