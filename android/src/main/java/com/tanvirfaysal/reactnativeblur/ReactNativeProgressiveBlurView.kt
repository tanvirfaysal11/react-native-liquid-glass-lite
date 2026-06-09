package com.tanvirfaysal.reactnativeblur

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Shader
import android.util.AttributeSet
import android.util.Log
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.widget.FrameLayout
import com.qmdeve.blurview.widget.BlurView
import kotlin.math.max

/**
 * Android implementation of the ProgressiveBlurView component.
 *
 * Combines a full-screen [BlurView] with a [LinearGradient] mask drawn in
 * [dispatchDraw] using [PorterDuff.Mode.DST_IN]. This produces a smooth
 * transition from fully blurred to fully transparent without requiring
 * QmBlurView's limited built-in gradient support.
 *
 * The blur capture root is swapped to the nearest Screen / ReactRootView
 * ancestor (same as [ReactNativeBlurView]) to prevent navigation artifacts.
 */
class ReactNativeProgressiveBlurView : FrameLayout {

    // ── Internal state ────────────────────────────────────────────────────────

    private var blurView: BlurView? = null
    private val gradientPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }

    private var currentBlurRadius = DEFAULT_BLUR_RADIUS
    private var currentBlurRounds = DEFAULT_BLUR_ROUNDS
    private var currentOverlayColor = Color.TRANSPARENT
    private var currentDirection = "topToBottom"
    private var currentStartOffset = 0f
    private var isBlurInitialized = false
    private var pendingInitRunnable: Runnable? = null
    private var pendingSwapRunnable: Runnable? = null

    companion object {
        private const val TAG = "ReactNativeProgressiveBlur"
        private const val DEFAULT_BLUR_RADIUS = 10f
        private const val DEFAULT_BLUR_ROUNDS = 5
        private const val MIN_BLUR_AMOUNT = 0f
        private const val MAX_BLUR_AMOUNT = 100f
        private const val MAX_BLUR_RADIUS = 100f
        private const val DEBUG = false

        private fun logDebug(msg: String) { if (DEBUG) Log.d(TAG, msg) }
        private fun logWarning(msg: String) { Log.w(TAG, msg) }
        private fun logError(msg: String, t: Throwable? = null) { Log.e(TAG, msg, t) }

        private fun mapBlurAmountToRadius(amount: Float): Float {
            if (amount.isNaN() || amount.isInfinite()) return DEFAULT_BLUR_RADIUS
            return (amount.coerceIn(MIN_BLUR_AMOUNT, MAX_BLUR_AMOUNT) / MAX_BLUR_AMOUNT) * MAX_BLUR_RADIUS
        }
    }

    constructor(context: Context) : super(context) { setupView() }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { setupView() }

    // ── Initialization ────────────────────────────────────────────────────────

    private fun setupView() {
        setWillNotDraw(false)
        super.setBackgroundColor(currentOverlayColor)
        initializeBlurChild()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isBlurInitialized) {
            val r = Runnable { pendingInitRunnable = null; initializeBlurChild() }
            pendingInitRunnable = r
            post(r)
        }
    }

    private fun initializeBlurChild() {
        if (isBlurInitialized) return
        try {
            if (blurView == null) {
                blurView = BlurView(context, null).apply {
                    layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                    setDownsampleFactor(6.0f)
                    blurRounds = currentBlurRounds
                }
                addView(blurView)
            }

            blurView?.apply {
                setBlurRadius(currentBlurRadius)
                overlayColor = currentOverlayColor
                setBackgroundColor(currentOverlayColor)
            }

            val swapR = Runnable { pendingSwapRunnable = null; swapBlurRootToScreenAncestor() }
            pendingSwapRunnable = swapR
            blurView?.post(swapR)

            isBlurInitialized = true
            logDebug("Initialized progressive blur")
            updateGradient()
        } catch (e: Exception) {
            logError("initializeBlurChild failed: ${e.message}", e)
        }
    }

    // ── Blur root swapping ────────────────────────────────────────────────────

    private fun swapBlurRootToScreenAncestor() {
        val bv = blurView ?: return
        val newRoot = findNearestScreenAncestor() ?: findNearestReactRootView() ?: return

        try {
            val oldDecor = bv.mDecorView
            val listener = bv.preDrawListener

            if (oldDecor == null || listener == null) return

            try { oldDecor.viewTreeObserver.removeOnPreDrawListener(listener) }
            catch (e: Exception) { logDebug("remove old listener: ${e.message}") }

            bv.mDecorView = newRoot
            newRoot.viewTreeObserver.addOnPreDrawListener(listener)
            bv.mDifferentRoot = newRoot.rootView != bv.rootView

            try {
                val field = bv.javaClass.superclass?.getDeclaredField("mForceRedraw")
                    ?.apply { isAccessible = true }
                field?.setBoolean(bv, true)
            } catch (e: NoSuchFieldException) {
                logWarning("mForceRedraw not found via reflection: ${e.message}")
            }

            logDebug("Swapped blur root to ${newRoot.javaClass.simpleName}")
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

    // ── Gradient ──────────────────────────────────────────────────────────────

    private fun updateGradient() {
        if (width <= 0 || height <= 0) return

        try {
            val shader = when (currentDirection) {
                "center" -> {
                    val startEdge = max(currentStartOffset, 0.01f)
                    val endEdge = 1f - startEdge
                    val centerLow = (0.2f + startEdge).coerceAtMost(0.5f)
                    val centerHigh = (0.8f - startEdge).coerceAtLeast(0.5f)
                    LinearGradient(
                        0f, 0f, 0f, height.toFloat(),
                        intArrayOf(
                            Color.TRANSPARENT, Color.TRANSPARENT,
                            Color.WHITE, Color.WHITE,
                            Color.TRANSPARENT, Color.TRANSPARENT,
                        ),
                        floatArrayOf(0f, startEdge, centerLow, centerHigh, endEdge, 1f),
                        Shader.TileMode.CLAMP,
                    )
                }
                else -> {
                    val (x0, y0, x1, y1) = when (currentDirection) {
                        "bottomToTop" -> {
                            val off = height * currentStartOffset
                            floatArrayOf(0f, 0f, 0f, height - off)
                        }
                        "leftToRight" -> {
                            val off = width * currentStartOffset
                            floatArrayOf(off, 0f, width.toFloat(), 0f)
                        }
                        "rightToLeft" -> {
                            val off = width * currentStartOffset
                            floatArrayOf(width.toFloat(), 0f, off, 0f)
                        }
                        else /* topToBottom */ -> {
                            val off = height * currentStartOffset
                            floatArrayOf(0f, height.toFloat(), 0f, off)
                        }
                    }
                    LinearGradient(
                        x0, y0, x1, y1,
                        intArrayOf(Color.TRANSPARENT, Color.WHITE),
                        floatArrayOf(0f, 1f),
                        Shader.TileMode.CLAMP,
                    )
                }
            }
            gradientPaint.shader = shader
            logDebug("Gradient updated: dir=$currentDirection offset=$currentStartOffset")
            invalidate()
        } catch (e: Exception) {
            logError("updateGradient failed: ${e.message}", e)
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (width <= 0 || height <= 0) { super.dispatchDraw(canvas); return }

        val save = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)
        super.dispatchDraw(canvas)

        gradientPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), gradientPaint)
        gradientPaint.xfermode = null

        canvas.restoreToCount(save)
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cleanup()
    }

    fun cleanup() {
        isBlurInitialized = false
        pendingInitRunnable?.let(::removeCallbacks)
        pendingInitRunnable = null
        pendingSwapRunnable?.let { blurView?.removeCallbacks(it) }
        pendingSwapRunnable = null

        blurView?.let { bv ->
            val listener = bv.preDrawListener
            val decor = bv.mDecorView
            if (listener != null && decor != null) {
                try { decor.viewTreeObserver.removeOnPreDrawListener(listener) }
                catch (e: Exception) { logDebug("cleanup removeListener: ${e.message}") }
            }
            bv.mDecorView = null
            bv.mDifferentRoot = false
        }

        logDebug("Cleaned up")
    }

    // ── Public setters ────────────────────────────────────────────────────────

    fun setBlurAmount(amount: Float) {
        var radius = mapBlurAmountToRadius(amount)
        if (currentDirection == "center") radius *= 0.35f   // center looks stronger; match iOS parity
        currentBlurRadius = radius
        logDebug("setBlurAmount $amount → radius $radius")
        try { blurView?.setBlurRadius(radius); invalidate() }
        catch (e: Exception) { logError("setBlurAmount failed: ${e.message}", e) }
    }

    fun setRounds(rounds: Int) {
        currentBlurRounds = rounds.coerceIn(1, 15)
        logDebug("setRounds → $currentBlurRounds")
        try { blurView?.blurRounds = currentBlurRounds }
        catch (e: Exception) { logError("setRounds failed: ${e.message}", e) }
    }

    fun setDirection(direction: String) {
        currentDirection = when (direction.lowercase()) {
            "blurredbottomcleartop", "bottomtotop", "bottom" -> "bottomToTop"
            "blurredtopclearbottom", "toptobottom", "top"   -> "topToBottom"
            "blurredcentercleartopandbottom", "center"       -> "center"
            "blurredlefttoclearright", "lefttoright", "left" -> "leftToRight"
            "blurredrighttoclearleft", "righttoleft", "right" -> "rightToLeft"
            else -> { logWarning("Unknown direction: $direction – defaulting to topToBottom"); "topToBottom" }
        }
        logDebug("setDirection → $currentDirection")
        try { updateGradient() }
        catch (e: Exception) { logError("setDirection failed: ${e.message}", e) }
    }

    fun setStartOffset(offset: Float) {
        currentStartOffset = offset.coerceIn(0f, 1f)
        logDebug("setStartOffset → $currentStartOffset")
        try { updateGradient() }
        catch (e: Exception) { logError("setStartOffset failed: ${e.message}", e) }
    }

    fun setBlurType(type: String) {
        currentOverlayColor = BlurType.fromString(type).overlayColor
        logDebug("setBlurType $type → overlay ${Integer.toHexString(currentOverlayColor)}")
        try { blurView?.setOverlayColor(currentOverlayColor); invalidate() }
        catch (e: Exception) { logError("setBlurType failed: ${e.message}", e) }
    }

    // ── Measure / Layout ──────────────────────────────────────────────────────

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(w, h)
        blurView?.measure(
            MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY),
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        blurView?.layout(0, 0, right - left, bottom - top)
        // Do NOT call super – children are positioned by React Native.
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) updateGradient()
    }
}
