package com.tanvirfaysal.reactnativeblur

import android.content.Context
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Path
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.ViewTreeObserver
import android.view.View.MeasureSpec
import androidx.core.graphics.toColorInt
import com.qmdeve.blurview.base.BaseBlurViewGroup
import com.qmdeve.blurview.widget.BlurViewGroup

/**
 * Android implementation of the React Native BlurView component.
 *
 * Uses [BlurViewGroup] from the QmBlurView library (high-performance
 * RenderScript / Vulkan blur). The blur capture root is redirected from the
 * activity decor view to the nearest `react-native-screens` Screen ancestor
 * via reflection, which prevents frame-capture artifacts during navigation
 * transitions.
 */
class ReactNativeBlurView : BlurViewGroup {

    // ── State ────────────────────────────────────────────────────────────────

    private var currentBlurRadius = DEFAULT_BLUR_RADIUS
    private var currentOverlayColor = Color.TRANSPARENT
    private var currentBlurRounds = DEFAULT_BLUR_ROUNDS
    private var currentBlurType: String = "xlight"

    private var borderRadius = 0f
    private var borderTopLeftRadius = -1f
    private var borderTopRightRadius = -1f
    private var borderBottomLeftRadius = -1f
    private var borderBottomRightRadius = -1f

    private var isBlurInitialized = false
    private var pendingInitRunnable: Runnable? = null

    // ── Constants ────────────────────────────────────────────────────────────

    companion object {
        private const val TAG = "ReactNativeBlurView"
        private const val DEFAULT_BLUR_RADIUS = 10f
        private const val DEFAULT_BLUR_ROUNDS = 5
        private const val MIN_BLUR_AMOUNT = 0f
        private const val MAX_BLUR_AMOUNT = 100f
        private const val MAX_BLUR_RADIUS = 100f
        private const val DEBUG = false

        private fun logDebug(msg: String) { if (DEBUG) Log.d(TAG, msg) }
        private fun logWarning(msg: String) { Log.w(TAG, msg) }
        private fun logError(msg: String, t: Throwable? = null) { Log.e(TAG, msg, t) }

        /** Maps the JS blur amount (0–100) to the QmBlurView radius (0–100). */
        private fun mapBlurAmountToRadius(amount: Float): Float =
            amount.coerceIn(MIN_BLUR_AMOUNT, MAX_BLUR_AMOUNT)
                .let { it / MAX_BLUR_AMOUNT * MAX_BLUR_RADIUS }
    }

    // ── Constructors ─────────────────────────────────────────────────────────

    constructor(context: Context?) : super(context, null) { setupView() }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) { setupView() }

    // ── Initialization ───────────────────────────────────────────────────────

    private fun setupView() {
        super.setBackgroundColor(currentOverlayColor)
        clipChildren = true
        clipToOutline = true
        blurRounds = currentBlurRounds
        super.setDownsampleFactor(6.0f)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isBlurInitialized) return
        swapBlurRootToScreenAncestor()
        initializeBlur()
    }

    private fun initializeBlur() {
        if (isBlurInitialized) return
        try {
            super.setBlurRadius(currentBlurRadius)
            super.setOverlayColor(currentOverlayColor)
            updateCornerRadius()
            isBlurInitialized = true
            logDebug("Initialized: radius=$currentBlurRadius overlay=$currentOverlayColor")
        } catch (e: Exception) {
            logError("Failed to initialize blur: ${e.message}", e)
        }
    }

    // ── Blur root swapping (prevents navigation flicker) ─────────────────────

    /**
     * Redirects QmBlurView's pre-draw capture from the activity decor view to
     * the nearest Screen (or ReactRootView) ancestor so that navigation
     * transitions do not produce stale blur frames.
     */
    private fun swapBlurRootToScreenAncestor() {
        val newRoot = findNearestScreenAncestor() ?: findNearestReactRootView() ?: return

        try {
            val blurViewGroupClass = BlurViewGroup::class.java
            val baseField = blurViewGroupClass.getDeclaredField("mBaseBlurViewGroup").apply {
                isAccessible = true
            }
            val base = baseField.get(this) ?: return
            val baseClass = BaseBlurViewGroup::class.java

            val decorViewField = baseClass.getDeclaredField("mDecorView").apply { isAccessible = true }
            val preDrawListenerField = baseClass.getDeclaredField("preDrawListener").apply { isAccessible = true }

            val oldDecor = decorViewField.get(base) as? View
            val listener = preDrawListenerField.get(base) as? ViewTreeObserver.OnPreDrawListener

            if (oldDecor == null || listener == null) {
                logWarning("swapBlurRoot: skipping – oldDecor or listener is null")
                return
            }

            try { oldDecor.viewTreeObserver.removeOnPreDrawListener(listener) }
            catch (e: Exception) { logDebug("Could not remove old listener: ${e.message}") }

            decorViewField.set(base, newRoot)
            newRoot.viewTreeObserver.addOnPreDrawListener(listener)

            baseClass.getDeclaredField("mDifferentRoot").apply { isAccessible = true }
                .setBoolean(base, newRoot.rootView != this.rootView)

            baseClass.getDeclaredField("mForceRedraw").apply { isAccessible = true }
                .setBoolean(base, true)

            logDebug("Swapped blur root to ${newRoot.javaClass.simpleName}")
        } catch (e: NoSuchFieldException) {
            logWarning("Reflection failed (field missing): ${e.message}")
        } catch (e: Exception) {
            logWarning("swapBlurRoot failed: ${e.message}")
        }
    }

    private fun findNearestScreenAncestor(): ViewGroup? {
        var p = parent
        while (p != null) {
            if (p.javaClass.name == "com.swmansion.rnscreens.Screen") return p as? ViewGroup
            p = p.parent
        }
        return null
    }

    private fun findNearestReactRootView(): ViewGroup? {
        var p = parent
        while (p != null) {
            if (p.javaClass.name == "com.facebook.react.ReactRootView") return p as? ViewGroup
            p = p.parent
        }
        return null
    }

    // ── Lifecycle ────────────────────────────────────────────────────────────

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cleanup()
    }

    /** Resets state and removes callbacks. Call from [onDropViewInstance]. */
    fun cleanup() {
        isBlurInitialized = false
        pendingInitRunnable?.let(::removeCallbacks)
        pendingInitRunnable = null
        logDebug("Cleaned up")
    }

    // ── Public setters (called by ViewManager) ────────────────────────────────

    fun setBlurAmount(amount: Float) {
        currentBlurRadius = mapBlurAmountToRadius(amount)
        logDebug("setBlurAmount $amount → radius $currentBlurRadius")
        try { super.setBlurRadius(currentBlurRadius) }
        catch (e: Exception) { logError("setBlurRadius failed: ${e.message}", e) }
    }

    fun setRounds(rounds: Int) {
        currentBlurRounds = rounds.coerceIn(1, 15)
        logDebug("setRounds $rounds → $currentBlurRounds")
        try { super.setBlurRounds(currentBlurRounds) }
        catch (e: Exception) { logError("setBlurRounds failed: ${e.message}", e) }
    }

    fun setBlurType(type: String) {
        currentBlurType = type
        currentOverlayColor = BlurType.fromString(type).overlayColor
        logDebug("setBlurType $type → overlay ${Integer.toHexString(currentOverlayColor)}")
        try {
            super.setBackgroundColor(currentOverlayColor)
            super.setOverlayColor(currentOverlayColor)
        } catch (e: Exception) { logError("setBlurType failed: ${e.message}", e) }
    }

    // ── Corner radius ────────────────────────────────────────────────────────

    fun setBorderRadius(radius: Float) { borderRadius = radius; updateCornerRadius() }
    fun setBorderTopLeftRadius(radius: Float) { borderTopLeftRadius = radius; updateCornerRadius() }
    fun setBorderTopRightRadius(radius: Float) { borderTopRightRadius = radius; updateCornerRadius() }
    fun setBorderBottomLeftRadius(radius: Float) { borderBottomLeftRadius = radius; updateCornerRadius() }
    fun setBorderBottomRightRadius(radius: Float) { borderBottomRightRadius = radius; updateCornerRadius() }

    private fun dpToPx(dp: Float): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)

    private fun updateCornerRadius() {
        try {
            val base = dpToPx(borderRadius)
            val tl = if (borderTopLeftRadius >= 0) dpToPx(borderTopLeftRadius) else base
            val tr = if (borderTopRightRadius >= 0) dpToPx(borderTopRightRadius) else base
            val bl = if (borderBottomLeftRadius >= 0) dpToPx(borderBottomLeftRadius) else base
            val br = if (borderBottomRightRadius >= 0) dpToPx(borderBottomRightRadius) else base

            super.setTopLeftCornerRadius(tl)
            super.setTopRightCornerRadius(tr)
            super.setBottomLeftCornerRadius(bl)
            super.setBottomRightCornerRadius(br)
            super.setCornerRadius(base)

            outlineProvider = if (tl == tr && tr == bl && bl == br) {
                object : ViewOutlineProvider() {
                    override fun getOutline(view: View, outline: Outline?) {
                        outline?.setRoundRect(0, 0, view.width, view.height, base)
                    }
                }
            } else {
                object : ViewOutlineProvider() {
                    override fun getOutline(view: View, outline: Outline?) {
                        val path = Path().apply {
                            addRoundRect(
                                0f, 0f, width.toFloat(), height.toFloat(),
                                floatArrayOf(tl, tl, tr, tr, br, br, bl, bl),
                                Path.Direction.CW,
                            )
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            outline?.setPath(path)
                        } else {
                            @Suppress("DEPRECATION")
                            outline?.setConvexPath(path)
                        }
                    }
                }
            }
            clipToOutline = true
        } catch (e: Exception) {
            logError("updateCornerRadius failed: ${e.message}", e)
        }
    }

    // ── Layout ───────────────────────────────────────────────────────────────

    override fun generateDefaultLayoutParams(): BlurViewGroup.LayoutParams =
        BlurViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

    override fun generateLayoutParams(attrs: AttributeSet?): BlurViewGroup.LayoutParams =
        BlurViewGroup.LayoutParams(context, attrs)

    override fun generateLayoutParams(p: ViewGroup.LayoutParams?): ViewGroup.LayoutParams =
        ViewGroup.MarginLayoutParams(p)

    override fun checkLayoutParams(p: ViewGroup.LayoutParams?): Boolean =
        p is ViewGroup.MarginLayoutParams

    /** Trust React Native / Yoga to supply the correct dimensions. */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            MeasureSpec.getSize(widthMeasureSpec),
            MeasureSpec.getSize(heightMeasureSpec),
        )
    }

    /** Layout is handled by React Native's UIManager – no-op here. */
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) = Unit
}
