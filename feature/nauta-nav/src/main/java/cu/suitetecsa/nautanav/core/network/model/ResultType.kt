package cu.suitetecsa.nautanav.core.network.model

sealed class ResultType<T> {
    data class Success<T>(val data: T) : ResultType<T>()
    data class Error<T>(val message: String) : ResultType<T>()
}
