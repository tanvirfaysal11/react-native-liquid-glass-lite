package com.tanvirfaysal.reactnativeblur

import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.viewmanagers.ReactNativeBlurViewManagerDelegate
import com.facebook.react.viewmanagers.ReactNativeBlurViewManagerInterface

@ReactModule(name = ReactNativeBlurViewManager.NAME)
class ReactNativeBlurViewManager :
    ViewGroupManager<ReactNativeBlurView>(),
    ReactNativeBlurViewManagerInterface<ReactNativeBlurView> {

    private val delegate = ReactNativeBlurViewManagerDelegate(this)

    override fun getDelegate(): ViewManagerDelegate<ReactNativeBlurView> = delegate
    override fun getName(): String = NAME

    override fun createViewInstance(context: ThemedReactContext): ReactNativeBlurView =
        ReactNativeBlurView(context)

    // ── Props ─────────────────────────────────────────────────────────────────

    @ReactProp(name = "blurType")
    override fun setBlurType(view: ReactNativeBlurView?, blurType: String?) {
        view?.setBlurType(blurType ?: "xlight")
    }

    @ReactProp(name = "blurAmount", defaultDouble = 10.0)
    override fun setBlurAmount(view: ReactNativeBlurView?, blurAmount: Double) {
        view?.setBlurAmount(blurAmount.toFloat())
    }

    @ReactProp(name = "blurRounds", defaultInt = 5)
    override fun setBlurRounds(view: ReactNativeBlurView?, blurRounds: Int) {
        view?.setRounds(blurRounds)
    }

    @ReactProp(name = "borderRadius", defaultFloat = 0f)
    override fun setBorderRadius(view: ReactNativeBlurView?, borderRadius: Float) {
        view?.setBorderRadius(borderRadius)
    }

    @ReactProp(name = "borderTopLeftRadius")
    override fun setBorderTopLeftRadius(view: ReactNativeBlurView?, v: Float) {
        view?.setBorderTopLeftRadius(v)
    }

    @ReactProp(name = "borderTopRightRadius")
    override fun setBorderTopRightRadius(view: ReactNativeBlurView?, v: Float) {
        view?.setBorderTopRightRadius(v)
    }

    @ReactProp(name = "borderBottomLeftRadius")
    override fun setBorderBottomLeftRadius(view: ReactNativeBlurView?, v: Float) {
        view?.setBorderBottomLeftRadius(v)
    }

    @ReactProp(name = "borderBottomRightRadius")
    override fun setBorderBottomRightRadius(view: ReactNativeBlurView?, v: Float) {
        view?.setBorderBottomRightRadius(v)
    }

    /** iOS-only prop – no-op on Android. */
    @ReactProp(name = "reducedTransparencyFallbackColor")
    override fun setReducedTransparencyFallbackColor(view: ReactNativeBlurView?, color: String?) = Unit

    /** iOS-only prop – no-op on Android. */
    @ReactProp(name = "ignoreSafeArea")
    override fun setIgnoreSafeArea(view: ReactNativeBlurView?, ignoreSafeArea: Boolean) = Unit

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    override fun onDropViewInstance(view: ReactNativeBlurView) {
        super.onDropViewInstance(view)
        view.cleanup()
    }

    /**
     * Return false so React Native's Yoga layout engine positions children
     * rather than this view manager.
     */
    override fun needsCustomLayoutForChildren(): Boolean = false

    companion object {
        const val NAME = "ReactNativeBlurView"
    }
}
