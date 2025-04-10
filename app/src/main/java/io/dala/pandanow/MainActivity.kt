package io.dala.pandanow

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.util.UnstableApi
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import io.dala.pandanow.presentation.VideoPlayerViewModel
import io.dala.pandanow.presentation.navigation.PandaNowNavHost
import io.dala.pandanow.presentation.theme.PandaNowTheme
import io.dala.pandanow.utils.inAppUpdate.AppUpdateChecker

@UnstableApi
class MainActivity : ComponentActivity() {
    private lateinit var consentInformation: ConsentInformation
    private val viewModel: VideoPlayerViewModel by lazy {
        ViewModelProvider(this).get(VideoPlayerViewModel::class.java)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        //Admob consent
        val params = ConsentRequestParameters
            .Builder()
            .build()
        consentInformation = UserMessagingPlatform.getConsentInformation(this)
        consentInformation.requestConsentInfoUpdate(
            this,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(this) { loadAndShowError ->
                    if (loadAndShowError != null) {
                        println(loadAndShowError.message)
                    }
                    if (consentInformation.canRequestAds()) {
                        MobileAds.initialize(this)
                    }
                }
            },
            {
                println(it.message)
            }
        )
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PandaNowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PandaNowNavHost()
                }
            }
        }
    }
    override fun onPause() {
        super.onPause()
        viewModel.saveCurrentPosition()
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        viewModel.setInPipMode(isInPictureInPictureMode)
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppUpdateChecker.APP_UPDATE_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}