package com.smileidentity.react.utils

import com.smileidentity.react.results.BiometricKycCaptureResult
import com.smileidentity.react.results.DocumentCaptureResult
import com.smileidentity.react.results.SmartSelfieCaptureResult
import com.squareup.moshi.Moshi
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Test

class ResultAdaptersTest {
  private val moshi = Moshi.Builder()
    .add(SelfieCaptureResultAdapter.FACTORY)
    .add(BiometricKycCaptureResultAdapter.FACTORY)
    .add(DocumentCaptureResultAdapter.FACTORY)
    .build()

  private fun files(count: Int) = List(count) { File("/tmp/liveness_$it.jpg") }

  @Test
  fun selfieResultPreservesLivenessCountAndOrder() {
    listOf(6, 7, 12).forEach { count ->
      val adapter = moshi.adapter(SmartSelfieCaptureResult::class.java)
      val json = adapter.toJson(
        SmartSelfieCaptureResult(selfieFile = File("/tmp/selfie.jpg"), livenessFiles = files(count)),
      )
      assertEquals(files(count).map { it.absolutePath }, adapter.fromJson(json)!!.livenessFiles!!.map { it.absolutePath })
    }
  }

  @Test
  fun biometricKycResultPreservesLivenessCountAndOrder() {
    listOf(6, 7).forEach { count ->
      val adapter = moshi.adapter(BiometricKycCaptureResult::class.java)
      val json = adapter.toJson(
        BiometricKycCaptureResult(selfieFile = File("/tmp/selfie.jpg"), livenessFiles = files(count)),
      )
      assertEquals(files(count).map { it.absolutePath }, adapter.fromJson(json)!!.livenessFiles!!.map { it.absolutePath })
    }
  }

  @Test
  fun documentResultPreservesLivenessCountAndOrder() {
    listOf(6, 7).forEach { count ->
      val adapter = moshi.adapter(DocumentCaptureResult::class.java)
      val json = adapter.toJson(
        DocumentCaptureResult(selfieFile = File("/tmp/selfie.jpg"), livenessFiles = files(count)),
      )
      assertEquals(files(count).map { it.absolutePath }, adapter.fromJson(json)!!.livenessFiles!!.map { it.absolutePath })
    }
  }
}
