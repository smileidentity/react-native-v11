package com.smileidentity.react.viewmanagers

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.smileidentity.react.utils.getBoolOrDefault
import com.smileidentity.react.utils.getImmutableMapOrDefault
import com.smileidentity.react.utils.getStringOrDefault
import com.smileidentity.react.views.SmileIDConsentView
import com.smileidentity.react.views.SmileIDDocumentCaptureView

@ReactModule(name = SmileIDConsentViewManager.NAME)
class SmileIDConsentViewManager(
  private val reactApplicationContext: ReactApplicationContext
) : BaseSmileIDViewManager<SmileIDConsentView>(reactApplicationContext) {

  override fun getName(): String = NAME

  override fun createSmileView(): SmileIDConsentView {
    return SmileIDConsentView(reactApplicationContext)
  }

  override fun applyArgs(view: SmileIDConsentView, args: ReadableMap?) {
    args?.let {

      val partnerName = it.getString("partnerName")
        ?: return view.emitFailure(IllegalArgumentException("partnerName is required to show Consent Screen"))
      val partnerPrivacyPolicy = it.getString("partnerPrivacyPolicy") ?: return view.emitFailure(
        IllegalArgumentException("partnerPrivacyPolicy is required to show Consent Screen")
      )
      val logoResName = it.getString("partnerIcon")
        ?: return view.emitFailure(IllegalArgumentException("partnerIcon is required to show Consent Screen"))
      val productName = it.getString("productName")
        ?: return view.emitFailure(IllegalArgumentException("productName is required to show Consent Screen"))

      view.extraPartnerParams = it.getImmutableMapOrDefault("extraPartnerParams")
      view.userId = it.getStringOrDefault("userId")
      view.jobId = it.getStringOrDefault("jobId")
      view.partnerName = partnerName
      view.partnerPrivacyPolicy = partnerPrivacyPolicy
      view.logoResName = logoResName
      view.productName = productName
      view.showAttribution = it.getBoolOrDefault("showAttribution", true)
      view.showInstructions = it.getBoolOrDefault("showInstructions", true)
    }
  }

  companion object {
    const val NAME = "SmileIDConsentView"
  }
}
