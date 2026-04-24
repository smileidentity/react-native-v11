package com.smileidentity.react.viewmanagers

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.smileidentity.react.toSmileSensitivity
import com.smileidentity.react.utils.getBoolOrDefault
import com.smileidentity.react.utils.getStringOrDefault
import com.smileidentity.react.views.SmileIDSmartSelfieCaptureView
import com.smileidentity.react.views.SmileIDSmartSelfieEnrollmentEnhancedView
import com.smileidentity.react.views.SmileIDView

@ReactModule(name = SmileIDSmartSelfieCaptureViewManager.NAME)
class SmileIDSmartSelfieCaptureViewManager(
  private val reactApplicationContext: ReactApplicationContext
) : BaseSmileIDViewManager<SmileIDSmartSelfieCaptureView>(reactApplicationContext) {
  override fun getName(): String = NAME

  override fun createSmileView(): SmileIDSmartSelfieCaptureView {
    return SmileIDSmartSelfieCaptureView(reactApplicationContext)
  }

  override fun applyArgs(view: SmileIDSmartSelfieCaptureView, args: ReadableMap?) {
    args?.let {
      view.userId = it.getStringOrDefault("userId")
      view.jobId = it.getStringOrDefault("jobId")
      view.allowAgentMode = it.getBoolOrDefault("allowAgentMode", false)
      view.forceAgentMode = it.getBoolOrDefault("forceAgentMode", false)
      view.showAttribution = it.getBoolOrDefault("showAttribution", true)
      view.showInstructions = it.getBoolOrDefault("showInstructions", true)
      view.useStrictMode = it.getBoolOrDefault("useStrictMode", false)
      view.showConfirmation =
        it.getBoolOrDefault("showConfirmation", true)
      view.smileSensitivity =
        it.getStringOrDefault("smileSensitivity", null)?.toSmileSensitivity()
    }
  }

  companion object {
    const val NAME = "SmileIDSmartSelfieCaptureView"
  }
}
