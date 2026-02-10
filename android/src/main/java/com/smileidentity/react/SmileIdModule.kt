package com.smileidentity.react

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableArray
import com.smileidentity.SmileID
import com.smileidentity.SmileIDCrashReporting
import com.smileidentity.SmileIdSpec
import com.smileidentity.metadata.models.WrapperSdkName
import com.smileidentity.models.AuthenticationResponse
import com.smileidentity.models.BiometricKycJobStatusResponse
import com.smileidentity.models.DocumentVerificationJobStatusResponse
import com.smileidentity.models.EnhancedDocumentVerificationJobStatusResponse
import com.smileidentity.models.EnhancedKycAsyncResponse
import com.smileidentity.models.EnhancedKycResponse
import com.smileidentity.models.PrepUploadResponse
import com.smileidentity.models.ProductsConfigResponse
import com.smileidentity.models.ServicesResponse
import com.smileidentity.models.SmartSelfieJobStatusResponse
import com.smileidentity.models.ValidDocumentsResponse
import com.smileidentity.networking.pollBiometricKycJobStatus
import com.smileidentity.networking.pollDocumentVerificationJobStatus
import com.smileidentity.networking.pollEnhancedDocumentVerificationJobStatus
import com.smileidentity.networking.pollSmartSelfieJobStatus
import com.smileidentity.react.utils.getIntOrDefault
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class SmileIdModule internal constructor(
  context: ReactApplicationContext,
) : SmileIdSpec(context) {
  override fun getName(): String = NAME

  @ReactMethod
  override fun initialize(
    useSandBox: Boolean,
    enableCrashReporting: Boolean,
    config: ReadableMap?,
    apiKey: String?,
    promise: Promise,
  ) {
    try {
      // Set wrapper info for React Native SDK
      try {
        val version = BuildConfig.SMILE_ID_VERSION
        SmileID.setWrapperInfo(WrapperSdkName.ReactNative, version)
      } catch (e: Exception) {
        // Fallback to default version if BuildConfig is not available
        SmileID.setWrapperInfo(WrapperSdkName.ReactNative, "unknown")
      }

      when {
        // Case 1: Initialize with API key and config
        apiKey != null && config != null -> {
          SmileID.initialize(
            context = reactApplicationContext,
            apiKey = apiKey,
            config = config.toConfig(),
            useSandbox = useSandBox,
            enableCrashReporting = enableCrashReporting,
          )
        }
        // Case 2: Initialize with just config
        config != null -> {
          SmileID.initialize(
            context = reactApplicationContext,
            config = config.toConfig(),
            useSandbox = useSandBox,
            enableCrashReporting = enableCrashReporting,
          )
        }
        // Case 3: Basic initialization
        else -> {
          SmileID.initialize(
            context = reactApplicationContext,
            useSandbox = useSandBox,
          )
        }
      }
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("INITIALIZE_ERROR", e.message, e)
    }
  }

  @ReactMethod
  override fun setCallbackUrl(
    callbackUrl: String,
    promise: Promise,
  ) {
    SmileID.setCallbackUrl(callbackUrl = URL(callbackUrl))
    promise.resolve(null)
  }

  @ReactMethod
  override fun disableCrashReporting(promise: Promise) {
    SmileIDCrashReporting.disable()
  }

  @ReactMethod
  override fun setAllowOfflineMode(
    allowOfflineMode: Boolean,
    promise: Promise,
  ) {
    SmileID.setAllowOfflineMode(allowOfflineMode)
    promise.resolve(null)
  }

  @ReactMethod
  override fun applyLocalisation(
    promise: Promise,
  ) {
    // On Android, localization is handled automatically via string resources.
    // Users provide translations in values-<lang>/strings.xml and Android
    // resolves them based on the device locale. No additional setup needed.
    promise.resolve(null)
  }

  @ReactMethod
  override fun submitJob(
    jobId: String,
    promise: Promise,
  ) = launch(
    work = { SmileID.submitJob(jobId) },
    clazz = Unit::class.java,
    promise = promise,
  )

  @ReactMethod
  override fun getUnsubmittedJobs(promise: Promise) {
    try {
      val writableArray: WritableArray = Arguments.createArray()
      SmileID.getUnsubmittedJobs().forEach {
        writableArray.pushString(it)
      }
      promise.resolve(writableArray)
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  @ReactMethod
  override fun getSubmittedJobs(promise: Promise) {
    try {
      val writableArray: WritableArray = Arguments.createArray()
      SmileID.getSubmittedJobs().forEach {
        writableArray.pushString(it)
      }
      promise.resolve(writableArray)
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  @ReactMethod
  override fun cleanup(
    jobId: String,
    promise: Promise,
  ) {
    try {
      SmileID.cleanup(jobId)
      promise.resolve(null)
    } catch (e: Exception) {
      promise.resolve(e)
    }
  }

  @ReactMethod
  override fun authenticate(
    request: ReadableMap,
    promise: Promise,
  ) = launch(
    work = {
      SmileID.api.authenticate(request = request.toAuthenticationRequest())
    },
    clazz = AuthenticationResponse::class.java,
    promise = promise,
  )

  @ReactMethod
  override fun prepUpload(
    request: ReadableMap,
    promise: Promise,
  ) = launch(
    work = { SmileID.api.prepUpload(headers = mapOf(), request = request.toPrepUploadRequest()) },
    clazz = PrepUploadResponse::class.java,
    promise = promise,
  )

  @ReactMethod
  override fun upload(
    url: String,
    request: ReadableMap,
    promise: Promise,
  ) = launch(
    work = { SmileID.api.upload(url, request.toUploadRequest()) },
    clazz = Unit::class.java,
    promise = promise,
  )

  @ReactMethod
  override fun doEnhancedKyc(
    request: ReadableMap,
    promise: Promise,
  ) = launch(
    work = { SmileID.api.doEnhancedKyc(request = request.toEnhancedKycRequest()) },
    clazz = EnhancedKycResponse::class.java,
    promise = promise,
  )

  @ReactMethod
  override fun doEnhancedKycAsync(
    request: ReadableMap,
    promise: Promise,
  ) = launch(
    work = { SmileID.api.doEnhancedKycAsync(request = request.toEnhancedKycRequest()) },
    clazz = EnhancedKycAsyncResponse::class.java,
    promise = promise,
  )

  @ReactMethod
  override fun getSmartSelfieJobStatus(
    request: ReadableMap,
    promise: Promise,
  ) = launch(
    work = { SmileID.api.getSmartSelfieJobStatus(request = request.toJobStatusRequest()) },
    clazz = SmartSelfieJobStatusResponse::class.java,
    promise = promise,
  )

  @ReactMethod
  override fun getDocumentVerificationJobStatus(
    request: ReadableMap,
    promise: Promise,
  ) = launch(
    work = { SmileID.api.getDocumentVerificationJobStatus(request = request.toJobStatusRequest()) },
    clazz = DocumentVerificationJobStatusResponse::class.java,
    promise = promise,
  )

  @ReactMethod
  override fun getBiometricKycJobStatus(
    request: ReadableMap,
    promise: Promise,
  ) = launch(
    work = { SmileID.api.getBiometricKycJobStatus(request = request.toJobStatusRequest()) },
    clazz = BiometricKycJobStatusResponse::class.java,
    promise = promise,
  )

  @ReactMethod
  override fun getEnhancedDocumentVerificationJobStatus(
    request: ReadableMap,
    promise: Promise,
  ) = launch(
    work = { SmileID.api.getEnhancedDocumentVerificationJobStatus(request = request.toJobStatusRequest()) },
    clazz = EnhancedDocumentVerificationJobStatusResponse::class.java,
    promise = promise,
  )

  @ReactMethod
  override fun getProductsConfig(
    request: ReadableMap,
    promise: Promise,
  ) = launch(
    work = { SmileID.api.getProductsConfig(request = request.toProductsConfigRequest()) },
    clazz = ProductsConfigResponse::class.java,
    promise = promise,
  )

  @ReactMethod
  override fun getValidDocuments(
    request: ReadableMap,
    promise: Promise,
  ) = launch(
    work = { SmileID.api.getValidDocuments(request = request.toProductsConfigRequest()) },
    clazz = ValidDocumentsResponse::class.java,
    promise = promise,
  )

  @ReactMethod
  override fun getServices(promise: Promise) =
    launch(
      work = { SmileID.api.getServices() },
      clazz = ServicesResponse::class.java,
      promise = promise,
    )

  @ReactMethod
  override fun pollSmartSelfieJobStatus(
    request: ReadableMap,
    promise: Promise,
  ) = launch(
    work = {
      val jobStatusRequest = request.toJobStatusRequest()
      val interval =
        request.getIntOrDefault("interval") ?: run {
          throw IllegalArgumentException("interval is required")
        }
      val numAttempts =
        request.getIntOrDefault("numAttempts") ?: run {
          throw IllegalArgumentException("numAttempts is required")
        }
      pollJobStatus(
        apiCall = SmileID.api::pollSmartSelfieJobStatus,
        request = jobStatusRequest,
        interval = interval.toLong(),
        numAttempts = numAttempts.toLong(),
      )
    },
    clazz = SmartSelfieJobStatusResponse::class.java,
    promise = promise,
  )

  @ReactMethod
  override fun pollDocumentVerificationJobStatus(
    request: ReadableMap,
    promise: Promise,
  ) = launch(
    work = {
      val jobStatusRequest = request.toJobStatusRequest()
      val interval =
        request.getIntOrDefault("interval") ?: run {
          throw IllegalArgumentException("interval is required")
        }
      val numAttempts =
        request.getIntOrDefault("numAttempts") ?: run {
          throw IllegalArgumentException("numAttempts is required")
        }
      pollJobStatus(
        apiCall = SmileID.api::pollDocumentVerificationJobStatus,
        request = jobStatusRequest,
        interval = interval.toLong(),
        numAttempts = numAttempts.toLong(),
      )
    },
    clazz = DocumentVerificationJobStatusResponse::class.java,
    promise = promise,
  )

  @ReactMethod
  override fun pollBiometricKycJobStatus(
    request: ReadableMap,
    promise: Promise,
  ) = launch(
    work = {
      val jobStatusRequest = request.toJobStatusRequest()
      val interval =
        request.getIntOrDefault("interval") ?: run {
          throw IllegalArgumentException("interval is required")
        }
      val numAttempts =
        request.getIntOrDefault("numAttempts") ?: run {
          throw IllegalArgumentException("numAttempts is required")
        }
      pollJobStatus(
        apiCall = SmileID.api::pollBiometricKycJobStatus,
        request = jobStatusRequest,
        interval = interval.toLong(),
        numAttempts = numAttempts.toLong(),
      )
    },
    clazz = BiometricKycJobStatusResponse::class.java,
    promise = promise,
  )

  @ReactMethod
  override fun pollEnhancedDocumentVerificationJobStatus(
    request: ReadableMap,
    promise: Promise,
  ) = launch(
    work = {
      val jobStatusRequest = request.toJobStatusRequest()
      val interval =
        request.getIntOrDefault("interval") ?: run {
          throw IllegalArgumentException("interval is required")
        }
      val numAttempts =
        request.getIntOrDefault("numAttempts") ?: run {
          throw IllegalArgumentException("numAttempts is required")
        }
      pollJobStatus(
        apiCall = SmileID.api::pollEnhancedDocumentVerificationJobStatus,
        request = jobStatusRequest,
        interval = interval.toLong(),
        numAttempts = numAttempts.toLong(),
      )
    },
    clazz = EnhancedDocumentVerificationJobStatusResponse::class.java,
    promise = promise,
  )

  private suspend fun <RequestType, ResponseType> pollJobStatus(
    apiCall: suspend (RequestType, Duration, Int) -> Flow<ResponseType>,
    request: RequestType,
    interval: Long,
    numAttempts: Long,
  ): ResponseType =
    try {
      val response =
        withContext(Dispatchers.IO) {
          apiCall(request, interval.milliseconds, numAttempts.toInt())
            .map {
              it
            }.last()
        }
      response
    } catch (e: Exception) {
      throw e
    }

  private fun <T> toJson(
    result: T,
    clazz: Class<T>,
  ): String {
    val adapter = SmileID.moshi.adapter(clazz)
    return adapter.toJson(result)
  }

  private fun <T> launch(
    work: suspend () -> T,
    clazz: Class<T>,
    promise: Promise,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
  ) {
    val handler =
      CoroutineExceptionHandler { _, throwable ->
        promise.reject(throwable)
      }
    scope.launch(handler) {
      try {
        val result = work()
        if (clazz == Unit::class.java) {
          promise.resolve(null)
        } else {
          val jsonResult = toJson(result, clazz)
          promise.resolve(jsonResult)
        }
      } catch (e: Exception) {
        promise.reject(e)
      }
    }
  }

  companion object {
    const val NAME = "RNSmileID"
  }
}
