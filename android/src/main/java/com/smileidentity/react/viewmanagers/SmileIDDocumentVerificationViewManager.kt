package com.smileidentity.react.viewmanagers

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.smileidentity.react.toAutoCapture
import com.smileidentity.react.toSmileSensitivity
import com.smileidentity.react.utils.getBoolOrDefault
import com.smileidentity.react.utils.getFloatOrDefault
import com.smileidentity.react.utils.getImmutableMapOrDefault
import com.smileidentity.react.utils.getIntOrDefault
import com.smileidentity.react.utils.getStringOrDefault
import com.smileidentity.react.views.SmileIDDocumentVerificationView
import com.smileidentity.react.views.SmileIDEnhancedDocumentVerificationView

@ReactModule(name = SmileIDDocumentVerificationViewManager.NAME)
class SmileIDDocumentVerificationViewManager(
  private val reactApplicationContext: ReactApplicationContext
) : BaseSmileIDViewManager<SmileIDDocumentVerificationView>(reactApplicationContext) {

  override fun getName(): String = NAME
  override fun createSmileView(): SmileIDDocumentVerificationView {
    return SmileIDDocumentVerificationView(reactApplicationContext)
  }

  override fun applyArgs(view: SmileIDDocumentVerificationView, args: ReadableMap?) {
    args?.let {
      val countryCode = it.getString("countryCode")
        ?: return view.emitFailure(IllegalArgumentException("countryCode is required to run Document Verification"))
      view.extraPartnerParams = it.getImmutableMapOrDefault("extraPartnerParams")
      view.userId = it.getStringOrDefault("userId")
      view.jobId = it.getStringOrDefault("jobId")
      view.countryCode = countryCode
      view.autoCaptureTimeout = it.getIntOrDefault("autoCaptureTimeout", null)
      view.autoCapture = it.getStringOrDefault("autoCapture", null)?.toAutoCapture()
      view.allowAgentMode = it.getBoolOrDefault("allowAgentMode", false)
      view.forceAgentMode = it.getBoolOrDefault("forceAgentMode", false)
      view.showAttribution = it.getBoolOrDefault("showAttribution", true)
      view.captureBothSides = it.getBoolOrDefault("captureBothSides", false)
      view.showInstructions = it.getBoolOrDefault("showInstructions", true)
      view.allowGalleryUpload = it.getBoolOrDefault("allowGalleryUpload", false)
      view.bypassSelfieCaptureWithFilePath = it.getStringOrDefault("bypassSelfieCaptureWithFilePath", null)
      view.documentType = it.getStringOrDefault("documentType", null)
      view.idAspectRatio = it.getFloatOrDefault("idAspectRatio")
      view.allowNewEnroll = it.getBoolOrDefault("allowNewEnroll", false)
      view.useStrictMode = it.getBoolOrDefault("useStrictMode", false)
      view.skipApiSubmission = it.getBoolOrDefault("skipApiSubmission", false)
      view.smileSensitivity =
        it.getStringOrDefault("smileSensitivity", null)?.toSmileSensitivity()
    }
  }

  companion object {
    const val NAME = "SmileIDDocumentVerificationView"
  }
}
