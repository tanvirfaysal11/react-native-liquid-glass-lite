// Web implementation of LiquidGlassView.
import React from 'react';
import type { LiquidGlassViewProps } from './LiquidGlassView';
import BlurView from './BlurView.web';
import { FALLBACK_BLUR_AMOUNT, getFallbackOverlayColor } from './colorUtils';

/**
 * Web implementation of LiquidGlassView.
 *
 * The iOS 26+ UIGlassEffect has no web equivalent, so this renders the same
 * enhanced-blur fallback the native component uses on Android and older iOS: a
 * strong BlurView with the glass tint scaled down to a subtle overlay.
 *
 * `glassType`, `isInteractive`, `ignoreSafeArea` and
 * `reducedTransparencyFallbackColor` are native-only and accepted for API
 * compatibility.
 */
export const LiquidGlassView: React.FC<LiquidGlassViewProps> = ({
  glassTintColor = 'clear',
  glassOpacity = 1.0,
  style,
  children,
  glassType: _glassType,
  reducedTransparencyFallbackColor: _reducedTransparencyFallbackColor,
  isInteractive: _isInteractive,
  ignoreSafeArea: _ignoreSafeArea,
  ...rest
}) => {
  const fallbackOverlayColor = getFallbackOverlayColor(
    glassTintColor,
    glassOpacity
  );

  return (
    <BlurView
      // "regular" is a subtle (~20% white) overlay; the default "xlight" would
      // make the glass look like a solid panel.
      blurType="regular"
      blurAmount={FALLBACK_BLUR_AMOUNT}
      overlayColor={fallbackOverlayColor}
      style={style}
      {...rest}
    >
      {children}
    </BlurView>
  );
};

export default LiquidGlassView;
