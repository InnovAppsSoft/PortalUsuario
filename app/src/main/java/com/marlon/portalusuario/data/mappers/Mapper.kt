package com.marlon.portalusuario.data.mappers

interface Mapper<in From, out To> {
    fun map(from: From): To
}
