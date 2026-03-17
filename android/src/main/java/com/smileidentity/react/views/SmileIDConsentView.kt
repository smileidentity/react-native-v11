package com.smileidentity.react.views

import android.webkit.URLUtil
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.facebook.react.bridge.ReactApplicationContext
import com.smileidentity.SmileID
import com.smileidentity.compose.ConsentScreen
import java.net.URL

class SmileIDConsentView(context: ReactApplicationContext) : SmileIDView(context) {
  var partnerName : String? = null
  var partnerPrivacyPolicy : String? = null
  var logoResName : String? = null
  var productName : String? = null

  override fun renderContent() {
    partnerName ?: run {
      emitFailure(IllegalArgumentException("partnerName is required for BiometricKYC"))
      return
    }
    partnerPrivacyPolicy?: run {
      emitFailure(IllegalArgumentException("partnerPrivacyPolicy is required for BiometricKYC"))
      return
    }
    if (!URLUtil.isValidUrl(partnerPrivacyPolicy)) {
      emitFailure(IllegalArgumentException("a valid url for partnerPrivacyPolicy is required for BiometricKYC"))
      return
    }
    logoResName ?: run {
      emitFailure(IllegalArgumentException("partnerIcon is required for BiometricKYC"))
      return
    }
    productName ?: run {
      emitFailure(IllegalArgumentException("productName is required for BiometricKYC"))
      return
    }
    val partnerIcon = context.resources.getIdentifier(
      logoResName,
      "drawable",
      (context as? ReactApplicationContext)?.currentActivity?.packageName
    )
    composeView.apply {
      val customViewModelStoreOwner = CustomViewModelStoreOwner()
      setContent {
        CompositionLocalProvider(LocalViewModelStoreOwner provides customViewModelStoreOwner) {
          SmileID.ConsentScreen(
            partnerIcon = painterResource(
              id = partnerIcon
            ),
            partnerName = partnerName!!,
            productName = productName!!,
            partnerPrivacyPolicy = URL(partnerPrivacyPolicy),
            onConsentDenied = {
              emitSuccess("denied")
            },
            onConsentGranted = {
              emitSuccess("accepted")
            },
            showAttribution = showAttribution
          )
        }
      }
    }
  }
}
