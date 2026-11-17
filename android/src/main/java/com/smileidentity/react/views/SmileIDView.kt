package com.smileidentity.react.views

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Choreographer
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.contains
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.WritableMap
import com.smileidentity.models.JobType
import com.smileidentity.models.SmileSensitivity
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import timber.log.Timber


interface SmileViewListener {
  fun emitSuccess(map: WritableMap)
}

@SuppressLint("CheckResult")
abstract class SmileIDView(private val currentContext: Context) : LinearLayout(currentContext) {

  lateinit var composeView: ComposeView
  var userId: String? = null
  var jobId: String? = null
  private var jobType: JobType? = null
  var allowAgentMode: Boolean? = false
  var forceAgentMode: Boolean? = false
  var smileViewListener: SmileViewListener? = null
  var allowNewEnroll: Boolean? = false
  var showInstructions: Boolean = true
  var skipApiSubmission: Boolean = false
  var showAttribution: Boolean = true
  var extraPartnerParams: ImmutableMap<String, String> = persistentMapOf()
  @Volatile private var released = false

  init {
    val layoutParams = ViewGroup.LayoutParams(
      LayoutParams.WRAP_CONTENT,
      LayoutParams.WRAP_CONTENT
    )
    setLayoutParams(layoutParams)
    orientation = VERTICAL
    render()

    setupLayoutHack()
    manuallyLayoutChildren()
  }

  private fun setUpViews() {
    if (::composeView.isInitialized && contains(composeView)) {
      removeView(composeView)
    }
    (context as ReactApplicationContext).currentActivity?.let {
      it.runOnUiThread {
        composeView = ComposeView(it)
        composeView.layoutParams = ViewGroup.LayoutParams(
          LayoutParams.MATCH_PARENT,
          LayoutParams.MATCH_PARENT
        )
        addView(composeView)
      }
    }
  }

  abstract fun renderContent()

  open fun render() {
    setUpViews()
    renderContent()
  }

  open fun emitSuccess(result: String) {
    val map = Arguments.createMap().apply {
      putString("result", result)
    }
    sendEvent(map)
  }

  open fun sendEvent(map: WritableMap) {
    val listener = smileViewListener ?: return
    listener.emitSuccess(map)
    // Kept after an error so a partner that stays on the screen still has a view; release() disposes it.
    if (map.hasKey("error")) return
    // The SDK's result screen is a Compose Dialog in its own window; it stays on top of
    // the next React Native screen until this composition is disposed.
    Handler(Looper.getMainLooper()).post { if (!released) disposeContent() }
  }

  fun disposeContent() {
    if (::composeView.isInitialized) composeView.disposeComposition()
  }

  fun release() {
    released = true
    disposeContent()
  }

  open fun emitFailure(error: Throwable?) {
    val map = Arguments.createMap()
    map.putString("error", error?.message ?: "Unknown error")
    sendEvent(map)
  }

  private fun setupLayoutHack() {
    Choreographer.getInstance().postFrameCallback(object : Choreographer.FrameCallback {
      override fun doFrame(frameTimeNanos: Long) {
        if (released) return
        manuallyLayoutChildren()
        viewTreeObserver.dispatchOnGlobalLayout()
        Choreographer.getInstance().postFrameCallback(this)
      }
    })
  }

  private fun manuallyLayoutChildren() {
    try {
      for (i in 0 until childCount) {
        val child = getChildAt(i)
        child.measure(
          MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY),
          MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY)
        )
        child.layout(0, 0, child.measuredWidth, child.measuredHeight)
      }
    } catch (e: Exception) {
      Timber.w(e)
    }
  }
}

