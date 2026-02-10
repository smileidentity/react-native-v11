package com.smileidentity

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReadableMap

abstract class SmileIdSpec internal constructor(context: ReactApplicationContext) :
  ReactContextBaseJavaModule(context) {

  abstract fun initialize(
    useSandBox: Boolean,
    enableCrashReporting: Boolean,
    config: ReadableMap?,
    apiKey: String?,
    promise: Promise
  )

  abstract fun setCallbackUrl(callbackUrl: String, promise: Promise)

  abstract fun setAllowOfflineMode(allowOfflineMode: Boolean ,promise: Promise)

  abstract fun submitJob(jobId: String ,promise: Promise)

  abstract fun getUnsubmittedJobs(promise: Promise)

  abstract fun getSubmittedJobs(promise: Promise)

  abstract fun cleanup(jobId: String ,promise: Promise)

  abstract fun disableCrashReporting(promise: Promise)

  abstract fun authenticate(request: ReadableMap, promise: Promise)

  abstract fun prepUpload(request: ReadableMap, promise: Promise)

  abstract fun upload(url: String, request: ReadableMap, promise: Promise)

  abstract fun doEnhancedKyc(request: ReadableMap, promise: Promise)

  abstract fun doEnhancedKycAsync(request: ReadableMap, promise: Promise)

  abstract fun getSmartSelfieJobStatus(request: ReadableMap, promise: Promise)

  abstract fun getDocumentVerificationJobStatus(request: ReadableMap, promise: Promise)

  abstract fun getBiometricKycJobStatus(request: ReadableMap, promise: Promise)

  abstract fun getEnhancedDocumentVerificationJobStatus(request: ReadableMap, promise: Promise)

  abstract fun getProductsConfig(request: ReadableMap, promise: Promise)

  abstract fun getValidDocuments(request: ReadableMap, promise: Promise)

  abstract fun getServices(promise: Promise)

  abstract fun pollSmartSelfieJobStatus(request: ReadableMap, promise: Promise)

  abstract fun pollDocumentVerificationJobStatus(request: ReadableMap, promise: Promise)

  abstract fun pollBiometricKycJobStatus(request: ReadableMap, promise: Promise)

  abstract fun pollEnhancedDocumentVerificationJobStatus(request: ReadableMap, promise: Promise)

  abstract fun applyLocalisation(promise: Promise)
}
