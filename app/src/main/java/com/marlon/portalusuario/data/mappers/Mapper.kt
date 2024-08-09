package com.marlon.portalusuario.data.mappers

interface Mapper<Domain, Entity, Api> {
    fun Api.toEntity(): Entity
    fun Entity.toDomain(): Domain
}
