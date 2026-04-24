package com.smileidentity.react.viewmanagers

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.module.annotations.ReactModule
import com.smileidentity.react.toSmileSensitivity
import com.smileidentity.react.utils.getBoolOrDefault
import com.smileidentity.react.utils.getImmutableMapOrDefault
import com.smileidentity.react.utils.getStringOrDefault
import com.smileidentity.react.views.SmileIDSmartSelfieEnrollmentView
import com.smileidentity.react.views.SmileIDView

@ReactModule(name = SmileIDSmartSelfieEnrollmentViewManager.NAME)
class SmileIDSmartSelfieEnrollmentViewManager(
  private val reactApplicationContext: ReactApplicationContext
) : BaseSmileIDViewManager<SmileIDSmartSelfieEnrollmentView>(reactApplicationContext) {
  override fun getName(): String = NAME

  override fun createSmileView(): SmileIDSmartSelfieEnrollmentView {
    return SmileIDSmartSelfieEnrollmentView(reactApplicationContext)
  }

  override fun applyArgs(view: SmileIDSmartSelfieEnrollmentView, args: ReadableMap?) {
    args?.let {
      view.extraPartnerParams = it.getImmutableMapOrDefault("extraPartnerParams")
      view.userId = it.getStringOrDefault("userId")
      view.jobId = it.getStringOrDefault("jobId")
      view.allowAgentMode = it.getBoolOrDefault("allowAgentMode", false)
      view.forceAgentMode = it.getBoolOrDefault("forceAgentMode", false)
      view.showAttribution = it.getBoolOrDefault("showAttribution", true)
      view.showInstructions = it.getBoolOrDefault("showInstructions", true)
      view.allowNewEnroll = it.getBoolOrDefault("allowNewEnroll", false)
      view.skipApiSubmission = it.getBoolOrDefault("skipApiSubmission", false)
      view.smileSensitivity =
        it.getStringOrDefault("smileSensitivity", null)?.toSmileSensitivity()
    }
  }

  companion object {
    const val NAME = "SmileIDSmartSelfieEnrollmentView"
  }
}
