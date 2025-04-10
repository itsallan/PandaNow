package io.dala.pandanow.utils.ads

interface ConsentCallback {
    fun onReadyForInitialization()
    fun onInitializationSuccess()
    fun onInitializationError(error: String)
    fun onConsentFormAvailability(available:Boolean)
    fun onConsentFormLoadSuccess()
    fun onConsentFormLoadFailure(error: String)
    fun onRequestShowConsentForm()
    fun onConsentFormShowFailure(error: String)
    fun onConsentFormDismissed()
    fun onConsentStatus(status: CMPStatus)
    fun onPolicyStatus(status: CMPStatus)
}