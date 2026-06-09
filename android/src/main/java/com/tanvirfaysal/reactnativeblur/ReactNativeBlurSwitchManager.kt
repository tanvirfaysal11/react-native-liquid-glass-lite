package com.tanvirfaysal.reactnativeblur

import android.graphics.Color
import androidx.core.graphics.toColorInt
import com.facebook.react.bridge.Arguments
import com.facebook.react.common.MapBuilder
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.uimanager.events.RCTEventEmitter

@ReactModule(name = ReactNativeBlurSwitchManager.NAME)
class ReactNativeBlurSwitchManager : SimpleViewManager<ReactNativeBlurSwitch>() {

    override fun getName(): String = NAME

    override fun createViewInstance(context: ThemedReactContext): ReactNativeBlurSwitch {
        return ReactNativeBlurSwitch(context).also { view ->
            view.setOnValueChangeListener { value ->
                context.getJSModule(RCTEventEmitter::class.java)
                    .receiveEvent(
                        view.id,
                        "topValueChange",
                        Arguments.createMap().apply { putBoolean("value", value) },
                    )
            }
        }
    }

    // ── Props ─────────────────────────────────────────────────────────────────

    @ReactProp(name = "value", defaultBoolean = false)
    fun setValue(view: ReactNativeBlurSwitch?, value: Boolean) {
        view?.setValue(value)
    }

    @ReactProp(name = "blurAmount", defaultDouble = 10.0)
    fun setBlurAmount(view: ReactNativeBlurSwitch?, blurAmount: Double) {
        view?.setBlurAmount(blurAmount.toFloat())
    }

    @ReactProp(name = "blurRounds", defaultInt = 5)
    fun setBlurRounds(view: ReactNativeBlurSwitch?, blurRounds: Int) {
        view?.setRounds(blurRounds)
    }

    @ReactProp(name = "thumbColor")
    fun setThumbColor(view: ReactNativeBlurSwitch?, color: String?) {
        color?.runCatching { view?.setThumbColor(toColorInt()) }
            ?.onFailure { android.util.Log.w(NAME, "Invalid thumbColor: $color") }
    }

    @ReactProp(name = "trackColorOff")
    fun setTrackColorOff(view: ReactNativeBlurSwitch?, color: String?) {
        color?.runCatching { view?.setTrackColorOff(toColorInt()) }
            ?.onFailure { android.util.Log.w(NAME, "Invalid trackColorOff: $color") }
    }

    @ReactProp(name = "trackColorOn")
    fun setTrackColorOn(view: ReactNativeBlurSwitch?, color: String?) {
        color?.runCatching { view?.setTrackColorOn(toColorInt()) }
            ?.onFailure { android.util.Log.w(NAME, "Invalid trackColorOn: $color") }
    }

    @ReactProp(name = "disabled", defaultBoolean = false)
    fun setDisabled(view: ReactNativeBlurSwitch?, disabled: Boolean) {
        view?.setDisabled(disabled)
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    override fun onDropViewInstance(view: ReactNativeBlurSwitch) {
        super.onDropViewInstance(view)
        view.cleanup()
    }

    // ── Events ────────────────────────────────────────────────────────────────

    override fun getExportedCustomDirectEventTypeConstants(): Map<String, Any> =
        MapBuilder.builder<String, Any>()
            .put("topValueChange", MapBuilder.of("registrationName", "onValueChange"))
            .build()

    companion object {
        const val NAME = "ReactNativeBlurSwitch"
    }
}
