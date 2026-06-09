package com.tanvirfaysal.reactnativeblur

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import com.qmdeve.blurview.widget.BlurSwitchButtonView

/**
 * Android implementation of the BlurSwitch component.
 *
 * Wraps [BlurSwitchButtonView] (QmBlurView) and exposes a React-Native-
 * compatible API. Note that [BlurSwitchButtonView] drives the on/off colors
 * automatically from the base (`trackColorOn`) color; thumb and off-track
 * colors are accepted for API parity but have no visual effect.
 */
class ReactNativeBlurSwitch : BlurSwitchButtonView {

    private var onValueChangeListener: ((Boolean) -> Unit)? = null
    private var currentValue = false
    private var isDisabled = false
    private var currentBlurRounds = DEFAULT_BLUR_ROUNDS

    companion object {
        private const val TAG = "ReactNativeBlurSwitch"
        private const val DEFAULT_BLUR_ROUNDS = 5
        private const val DEFAULT_WIDTH_DP = 65f
        private const val DEFAULT_HEIGHT_DP = 36f
        private const val MIN_BLUR_AMOUNT = 0f
        private const val MAX_BLUR_AMOUNT = 100f
        private const val MAX_BLUR_RADIUS = 25f
        private const val DEBUG = false

        private fun logDebug(msg: String) { if (DEBUG) Log.d(TAG, msg) }
        private fun logError(msg: String, t: Throwable? = null) { Log.e(TAG, msg, t) }

        /** Maps a JS blur amount (0–100) to the library's radius range (0–25). */
        private fun mapBlurAmountToRadius(amount: Float): Float =
            (amount.coerceIn(MIN_BLUR_AMOUNT, MAX_BLUR_AMOUNT) / MAX_BLUR_AMOUNT) * MAX_BLUR_RADIUS
    }

    constructor(context: Context?) : super(context, null) { initializeSwitch() }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) { initializeSwitch() }

    private fun initializeSwitch() {
        try {
            blurRounds = currentBlurRounds
            setOnCheckedChangeListener { isChecked ->
                if (isDisabled) {
                    // Revert optimistic toggle
                    setChecked(currentValue, false)
                    return@setOnCheckedChangeListener
                }
                if (currentValue != isChecked) {
                    currentValue = isChecked
                    onValueChangeListener?.invoke(isChecked)
                    logDebug("value changed → $isChecked")
                }
            }
            logDebug("initialized")
        } catch (e: Exception) {
            logError("initialization failed: ${e.message}", e)
        }
    }

    // ── Public API ────────────────────────────────────────────────────────────

    fun setValue(value: Boolean) {
        if (currentValue != value) {
            currentValue = value
            setChecked(value, false)
            logDebug("setValue → $value")
        }
    }

    fun setOnValueChangeListener(listener: ((Boolean) -> Unit)?) {
        onValueChangeListener = listener
    }

    /** API-parity stub – not supported by [BlurSwitchButtonView]. */
    fun setThumbColor(color: Int) {
        logDebug("setThumbColor not supported by BlurSwitchButtonView")
    }

    /** API-parity stub – not supported by [BlurSwitchButtonView]. */
    fun setTrackColorOff(color: Int) {
        logDebug("setTrackColorOff not supported by BlurSwitchButtonView")
    }

    fun setTrackColorOn(color: Int) {
        try { setBaseColor(color) }
        catch (e: Exception) { logError("setTrackColorOn failed: ${e.message}", e) }
    }

    fun setBlurAmount(amount: Float) {
        val radius = mapBlurAmountToRadius(amount)
        logDebug("setBlurAmount $amount → radius $radius")
        try { setBlurRadius(radius) }
        catch (e: Exception) { logError("setBlurRadius failed: ${e.message}", e) }
    }

    fun setRounds(rounds: Int) {
        currentBlurRounds = rounds.coerceIn(1, 15)
        logDebug("setRounds $rounds → $currentBlurRounds")
        try { super.setBlurRounds(currentBlurRounds) }
        catch (e: Exception) { logError("setBlurRounds failed: ${e.message}", e) }
    }

    fun setDisabled(disabled: Boolean) {
        isDisabled = disabled
        isEnabled = !disabled
        isClickable = !disabled
        alpha = if (disabled) 0.5f else 1f
        logDebug("setDisabled → $disabled")
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cleanup()
    }

    fun cleanup() {
        setOnCheckedChangeListener(null)
        onValueChangeListener = null
        logDebug("cleaned up")
    }

    // ── Touch ─────────────────────────────────────────────────────────────────

    override fun onTouchEvent(event: MotionEvent?): Boolean =
        if (isDisabled) false else super.onTouchEvent(event)

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean =
        if (isDisabled) false else super.dispatchTouchEvent(event)

    // ── Measure ──────────────────────────────────────────────────────────────

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val dm = context.resources.displayMetrics
        val defaultW = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_WIDTH_DP, dm).toInt()
        val defaultH = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_HEIGHT_DP, dm).toInt()
        setMeasuredDimension(
            resolveSize(defaultW, widthMeasureSpec),
            resolveSize(defaultH, heightMeasureSpec),
        )
    }
}
