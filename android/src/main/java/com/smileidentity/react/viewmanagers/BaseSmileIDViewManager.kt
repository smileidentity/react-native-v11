package com.smileidentity.react.viewmanagers

import android.view.Choreographer
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import java.util.Collections
import java.util.WeakHashMap
import com.facebook.react.ReactActivity
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.smileidentity.react.fragments.SmileCaptureFragment
import com.smileidentity.react.views.SmileIDView

abstract class BaseSmileIDViewManager<T : SmileIDView>(
  private val reactApplicationContext: ReactApplicationContext
) : SimpleViewManager<FrameLayout>() {
  private var smileCaptureFragment: SmileCaptureFragment? = null
  protected var smileIDView: T? = null
  private val droppedRoots: MutableSet<View> = Collections.newSetFromMap(WeakHashMap())

  override fun getExportedCustomBubblingEventTypeConstants(): Map<String, Any> {
    return mapOf(
      "onSmileResult" to mapOf(
        "phasedRegistrationNames" to mapOf(
          "bubbled" to "onResult"
        )
      )
    )
  }

  private fun createFragment(
    root: FrameLayout,
    reactNativeViewId: Int,
  ) {
    val parentView = root.findViewById<ViewGroup>(reactNativeViewId)
    setupLayout(parentView)

    // Clean up existing view and fragment
    smileIDView?.let { existingView ->
      existingView.release()
      (existingView.parent as? ViewGroup)?.removeView(existingView)
    }
    smileCaptureFragment?.let { existingFragment ->
      val activity = reactApplicationContext.currentActivity as ReactActivity
      val manager = activity.supportFragmentManager
      manager.beginTransaction().remove(existingFragment).commit()
      manager.executePendingTransactions()
    }

    // Create new instances
    smileIDView = createSmileView()
    smileCaptureFragment = SmileCaptureFragment()
    smileCaptureFragment?.setReactContext(reactApplicationContext)
    smileIDView?.let { view ->
      (view.parent as? ViewGroup)?.removeView(view)  // Ensure view is detached
      smileCaptureFragment?.setSmileIDView(view)
    }

    // Add new fragment
    smileCaptureFragment?.let {
      val activity = reactApplicationContext.currentActivity as ReactActivity
      val manager = activity.supportFragmentManager
      manager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
      manager.beginTransaction()
        .replace(reactNativeViewId, it, reactNativeViewId.toString())
        .commit()
    }
  }

  abstract fun createSmileView(): T
  abstract fun applyArgs(view: T, args: ReadableMap?)

  private fun setupLayout(view: View) {
    Choreographer.getInstance().postFrameCallback(object : Choreographer.FrameCallback {
      override fun doFrame(frameTimeNanos: Long) {
        if (droppedRoots.contains(view)) return
        manuallyLayoutChildren(view)
        view.viewTreeObserver.dispatchOnGlobalLayout()
        Choreographer.getInstance().postFrameCallback(this)
      }
    })
  }

  private fun manuallyLayoutChildren(view: View) {
    val width = view.measuredWidth
    val height = view.measuredHeight
    view.measure(
      MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
    )
    view.layout(view.left, view.top, view.left + width, view.top + height)
  }

  override fun createViewInstance(p0: ThemedReactContext): FrameLayout {
    return FrameLayout(p0)
  }

  override fun onDropViewInstance(view: FrameLayout) {
    super.onDropViewInstance(view)
    droppedRoots.add(view)
    val currentView = smileIDView ?: return
    if (currentView.parent !== view) return
    currentView.release()
    smileCaptureFragment?.let { fragment ->
      try {
        (reactApplicationContext.currentActivity as? ReactActivity)?.supportFragmentManager
          ?.beginTransaction()?.remove(fragment)?.commitAllowingStateLoss()
      } catch (_: IllegalStateException) {
      }
    }
    smileCaptureFragment = null
    smileIDView = null
  }

  override fun getCommandsMap(): Map<String, Int> {
    return mapOf("setParams" to COMMAND_SET_PARAMS, "create" to COMMAND_CREATE)
  }

  override fun receiveCommand(
    view: FrameLayout,
    commandId: String?,
    args: ReadableArray?
  ) {
    super.receiveCommand(view, commandId, args)
    handleCommand(view, commandId?.let { it.toIntOrNull() ?: getCommandsMap()[it] }, args)
  }

  @Deprecated("The New Architecture delivers numeric command ids through this overload")
  override fun receiveCommand(view: FrameLayout, commandId: Int, args: ReadableArray?) {
    handleCommand(view, commandId, args)
  }

  private fun handleCommand(view: FrameLayout, commandId: Int?, args: ReadableArray?) {
    when (commandId) {
      COMMAND_CREATE -> {
        val reactNativeViewId = requireNotNull(args).getInt(0)
        createFragment(view, reactNativeViewId)
      }

      COMMAND_SET_PARAMS -> {
        val params = args?.getMap(1)
        smileIDView?.let { view ->
          applyArgs(view, params)
          // Update the fragment with new params
          smileCaptureFragment?.updateViewWithParams(params)
        }
      }
    }
  }

  companion object {
    const val COMMAND_SET_PARAMS = 1
    const val COMMAND_CREATE = 2
  }
}
