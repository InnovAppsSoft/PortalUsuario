package cu.suitetecsa.nautanav.domain.model

import cu.suitetecsa.nautanav.util.NavigationType

data class UserModel(
    val id: Int,
    val username: String,
    var password: String,
    var remainingTime: Int,
    var credit: Float,
    val email: String,
    val serviceType: NavigationType,
    val accountType: String,
    val blockingDate: String,
    val dateOfElimination: String,
    val offer: String? = null,
    val phone: String? = null,
    val uploadSpeeds: String? = null,
    val downloadSpeeds: String? = null,
    val linkIdentifiers: String? = null,
    val linkStatus: String? = null,
    val activationDate: String? = null,
    val blockingDateHome: String? = null,
    val dateOfEliminationHome: String? = null,
    val monthlyFee: String? = null,
    val quotePaid: String? = null,
    val voucher: String? = null,
    val debt: String? = null
)