package com.tanvirfaysal.reactnativeblur

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager

/**
 * React Native package that registers all blur view managers.
 *
 * Add this to your `MainApplication.getPackages()` list (or use auto-linking
 * on React Native 0.60+).
 */
class ReactNativeBlurViewPackage : ReactPackage {

    override fun createViewManagers(
        reactContext: ReactApplicationContext,
    ): List<ViewManager<*, *>> = listOf(
        ReactNativeBlurViewManager(),
        ReactNativeProgressiveBlurViewManager(),
        ReactNativeBlurSwitchManager(),
    )

    override fun createNativeModules(
        reactContext: ReactApplicationContext,
    ): List<NativeModule> = emptyList()
}
