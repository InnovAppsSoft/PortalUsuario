package com.marlon.portalusuario.data.mappers

import com.marlon.portalusuario.domain.model.MobilePlan as Domain
import io.github.suitetecsa.sdk.nauta.model.MobilePlan as Api

class MobPlanApiToModelMapper : Mapper<Api, Domain> {
    override fun map(from: Api) = Domain(
        data = from.data,
        type = from.type,
        expires = from.expires
    )
}
