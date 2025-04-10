package io.dala.pandanow.utils.ads

import android.app.Activity
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.launch


@Composable
fun RewardedAdButton(
    context: Context,
    rewardedAd: RewardedAd?,
    onAdDismissed: () -> Unit,
    modifier: Modifier = Modifier,
    totalSize: String
) {
    val scope = rememberCoroutineScope()
    val (_, setRewardedAd) = remember { mutableStateOf<RewardedAd?>(null) }

    Button(
        enabled = rewardedAd != null,
        onClick = {
            scope.launch {
                showRewardedAd(
                    context = context,
                    rewardedAd = rewardedAd,
                    setRewardedAd = setRewardedAd,
                    onAdDismissed = onAdDismissed
                )
            }
        },
        modifier = modifier
    ) {
        Icon(imageVector = Icons.Outlined.Download, contentDescription = "")
        Text(
            totalSize,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

fun showRewardedAd(
    context: Context,
    rewardedAd: RewardedAd?,
    setRewardedAd: (RewardedAd?) -> Unit,
    onAdDismissed: () -> Unit
) {
    if (rewardedAd != null) {
        rewardedAd.show(context as Activity, OnUserEarnedRewardListener {
            // showToast(context, "Download Started")
            loadRewardedAd(context,   "", setRewardedAd)
            onAdDismissed()
        })
    } else {
        loadRewardedAd(context,  "", setRewardedAd)
    }
}

fun loadRewardedAd(
    context: Context,
    adUnitId: String,
    setRewardedAd: (RewardedAd?) -> Unit
) {
    RewardedAd.load(
        context,
        adUnitId,
        AdRequest.Builder().build(),
        object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                setRewardedAd(null)
            }

            override fun onAdLoaded(ad: RewardedAd) {
                setRewardedAd(ad)
            }
        }
    )
}