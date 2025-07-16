package io.dala.pandanow

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.util.UnstableApi
import io.dala.pandanow.presentation.VideoPlayerViewModel
import io.dala.pandanow.presentation.navigation.PandaNowNavHost
import io.dala.pandanow.presentation.theme.PandaNowTheme

@UnstableApi
class MainActivity : ComponentActivity() {
    private val viewModel: VideoPlayerViewModel by lazy {
        ViewModelProvider(this).get(VideoPlayerViewModel::class.java)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
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
}