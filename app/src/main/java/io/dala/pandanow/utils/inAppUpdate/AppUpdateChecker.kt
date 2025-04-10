package io.dala.pandanow.utils.inAppUpdate

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppUpdateChecker(private val context: Context) {

    suspend fun checkForUpdates(activity: Activity) {
        withContext(Dispatchers.IO) {
            try {
                val appUpdateManager = AppUpdateManagerFactory.create(context)
                val appUpdateInfoTask = appUpdateManager.appUpdateInfo

                appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                    ) {
                        try {
                            appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                activity,
                                AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build(),
                                APP_UPDATE_REQUEST_CODE
                            )
                        } catch (e: Exception) {
                            showToast("Update failed to start: ${e.message}")
                        }
                    } else {
                        showToast("No update available")
                    }
                }.addOnFailureListener { e ->
                    showToast("Failed to check for updates: ${e.message}")
                }
            } catch (e: Exception) {
                showToast("Error checking for updates: ${e.message}")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val APP_UPDATE_REQUEST_CODE = 500
    }
}