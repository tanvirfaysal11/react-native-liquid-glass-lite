package com.tanvirfaysal.reactnativeblur

import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.viewmanagers.ReactNativeProgressiveBlurViewManagerDelegate
import com.facebook.react.viewmanagers.ReactNativeProgressiveBlurViewManagerInterface

@ReactModule(name = ReactNativeProgressiveBlurViewManager.NAME)
class ReactNativeProgressiveBlurViewManager :
    SimpleViewManager<ReactNativeProgressiveBlurView>(),
    ReactNativeProgressiveBlurViewManagerInterface<ReactNativeProgressiveBlurView> {

    private val delegate = ReactNativeProgressiveBlurViewManagerDelegate(this)

    override fun getDelegate(): ViewManagerDelegate<ReactNativeProgressiveBlurView> = delegate
    override fun getName(): String = NAME

    override fun createViewInstance(context: ThemedReactContext): ReactNativeProgressiveBlurView =
        ReactNativeProgressiveBlurView(context)

    // ── Props ─────────────────────────────────────────────────────────────────

    @ReactProp(name = "blurType")
    override fun setBlurType(view: ReactNativeProgressiveBlurView?, blurType: String?) {
        view?.setBlurType(blurType.takeUnless { it.isNullOrBlank() } ?: "regular")
    }

    @ReactProp(name = "blurAmount", defaultDouble = 20.0)
    override fun setBlurAmount(view: ReactNativeProgressiveBlurView?, blurAmount: Double) {
        view?.setBlurAmount(blurAmount.toFloat())
    }

    @ReactProp(name = "blurRounds", defaultInt = 5)
    override fun setBlurRounds(view: ReactNativeProgressiveBlurView?, blurRounds: Int) {
        view?.setRounds(blurRounds)
    }

    @ReactProp(name = "direction")
    override fun setDirection(view: ReactNativeProgressiveBlurView?, direction: String?) {
        view?.setDirection(direction.takeUnless { it.isNullOrBlank() } ?: "blurredTopClearBottom")
    }

    @ReactProp(name = "startOffset", defaultDouble = 0.0)
    override fun setStartOffset(view: ReactNativeProgressiveBlurView?, startOffset: Double) {
        view?.setStartOffset(startOffset.toFloat())
    }

    /** iOS-only prop – no-op on Android. */
    @ReactProp(name = "reducedTransparencyFallbackColor")
    override fun setReducedTransparencyFallbackColor(
        view: ReactNativeProgressiveBlurView?,
        reducedTransparencyFallbackColor: String?,
    ) = Unit

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    override fun onDropViewInstance(view: ReactNativeProgressiveBlurView) {
        super.onDropViewInstance(view)
        view.cleanup()
    }

    companion object {
        const val NAME = "ReactNativeProgressiveBlurView"
    }
}
