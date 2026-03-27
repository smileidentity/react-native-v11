package com.smileidentity.react.views

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.facebook.react.bridge.ReactApplicationContext
import com.smileidentity.SmileID
import com.smileidentity.compose.DocumentVerification
import com.smileidentity.models.AutoCapture
import com.smileidentity.models.SmileSensitivity
import com.smileidentity.react.results.DocumentCaptureResult
import com.smileidentity.react.utils.DocumentCaptureResultAdapter
import com.smileidentity.results.SmileIDResult
import com.smileidentity.util.randomJobId
import com.smileidentity.util.randomUserId
import java.io.File
import kotlin.time.Duration.Companion.seconds

class SmileIDDocumentVerificationView(context: ReactApplicationContext) : SmileIDView(context) {
  var countryCode: String? = null

  var autoCaptureTimeout: Int? = null
  var autoCapture: AutoCapture? = null
  var allowGalleryUpload: Boolean = false
  var captureBothSides: Boolean = true
  var bypassSelfieCaptureWithFilePath: String? = null
  var documentType: String? = null
  var idAspectRatio: Float? = null
  var useStrictMode: Boolean? = false

  var smileSensitivity: SmileSensitivity? = null

  override fun renderContent() {
    countryCode ?: run {
      emitFailure(IllegalArgumentException("countryCode is required for DocumentVerification"))
      return
    }
    var bypassSelfieCaptureWithFile: File? = null
    bypassSelfieCaptureWithFilePath?.let {
      bypassSelfieCaptureWithFile = File(it)
    }
    composeView.apply {
      val customViewModelStoreOwner = CustomViewModelStoreOwner()
      setContent {
        CompositionLocalProvider(LocalViewModelStoreOwner provides customViewModelStoreOwner) {
          SmileID.DocumentVerification(
            userId = userId ?: rememberSaveable { randomUserId() },
            jobId = jobId ?: rememberSaveable { randomJobId() },
            countryCode = countryCode!!,
            autoCaptureTimeout = autoCaptureTimeout?.seconds ?: 10.seconds,
            autoCapture = autoCapture ?: AutoCapture.AutoCapture,
            documentType = documentType,
            idAspectRatio = idAspectRatio,
            showAttribution = showAttribution,
            allowAgentMode = allowAgentMode ?: false,
            forceAgentMode = forceAgentMode ?: false,
            showInstructions = showInstructions,
            allowGalleryUpload = allowGalleryUpload,
            captureBothSides = captureBothSides,
            allowNewEnroll = allowNewEnroll ?: false,
            bypassSelfieCaptureWithFile = bypassSelfieCaptureWithFile,
            smileSensitivity = smileSensitivity ?: SmileSensitivity.NORMAL,
            extraPartnerParams = extraPartnerParams,
            useStrictMode = useStrictMode ?: false,
            skipApiSubmission = skipApiSubmission
          ) { res ->
            when (res) {
              is SmileIDResult.Success -> {
                val result =
                  DocumentCaptureResult(
                    selfieFile = res.data.selfieFile,
                    documentFrontFile = res.data.documentFrontFile,
                    livenessFiles = res.data.livenessFiles,
                    documentBackFile = res.data.documentBackFile,
                    didSubmitDocumentVerificationJob = res.data.didSubmitDocumentVerificationJob,
                  )
                val newMoshi =
                  SmileID.moshi
                    .newBuilder()
                    .add(DocumentCaptureResultAdapter.FACTORY)
                    .build()
                val json =
                  try {
                    newMoshi
                      .adapter(DocumentCaptureResult::class.java)
                      .toJson(result)
                  } catch (e: Exception) {
                    emitFailure(e)
                    return@DocumentVerification
                  }
                json?.let { js ->
                  emitSuccess(js)
                }
              }

              is SmileIDResult.Error -> emitFailure(res.throwable)
            }
          }
        }
      }
    }
  }
}
