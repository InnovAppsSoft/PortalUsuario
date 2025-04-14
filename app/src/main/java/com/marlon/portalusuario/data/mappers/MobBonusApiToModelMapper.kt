package com.marlon.portalusuario.data.mappers

import com.marlon.portalusuario.domain.model.MobileBonus as Domain
import io.github.suitetecsa.sdk.nauta.model.MobileBonus as Api

class MobBonusApiToModelMapper : Mapper<Api, Domain> {
    override fun map(from: Api) = Domain(
        data = from.data,
        startDate = from.startDate,
        type = from.type,
        expires = from.expires
    )
}
