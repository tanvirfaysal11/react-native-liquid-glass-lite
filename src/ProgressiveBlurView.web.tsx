// Web implementation of ProgressiveBlurView.
import React from 'react';
import { StyleSheet, View } from 'react-native';
import type { ProgressiveBlurViewProps } from './ProgressiveBlurView';
import {
  BLUR_TYPE_TO_BACKGROUND,
  getProgressiveLayers,
  getProgressiveTintGradient,
  useBackdropFilterSupport,
} from './webBlurUtils';
import type { WebBlurStyle } from './webBlurUtils';

/**
 * Web implementation of ProgressiveBlurView.
 *
 * A single CSS `backdrop-filter` cannot vary its radius across an element, so
 * this stacks several absolutely-filled backdrop-filter layers, each masked
 * with a `linear-gradient` band and blurring at half the radius of the one
 * before. Composited, they produce a genuine blur-radius ramp matching the
 * native variable blur. A gradient of the `blurType` tint fades along the same
 * direction, and children render above everything, sharp.
 *
 * Without backdrop-filter support only the tint gradient remains. `blurRounds`
 * and `reducedTransparencyFallbackColor` are native-only and accepted for API
 * compatibility.
 */
export const ProgressiveBlurView: React.FC<ProgressiveBlurViewProps> = ({
  blurType = 'regular',
  blurAmount = 20,
  direction = 'blurredTopClearBottom',
  startOffset = 0.0,
  overlayColor,
  style,
  children,
  blurRounds: _blurRounds,
  reducedTransparencyFallbackColor: _reducedTransparencyFallbackColor,
  ...rest
}) => {
  const canUseBackdropFilter = useBackdropFilterSupport();

  const blurLayerStyles: WebBlurStyle[] = canUseBackdropFilter
    ? getProgressiveLayers(direction, startOffset, blurAmount).map((layer) => {
        const filter = `blur(${layer.radius}px)`;
        return {
          backdropFilter: filter,
          WebkitBackdropFilter: filter,
          maskImage: layer.maskImage,
          WebkitMaskImage: layer.maskImage,
        };
      })
    : [];

  const tintLayerStyle: WebBlurStyle = {
    backgroundImage: getProgressiveTintGradient(
      direction,
      startOffset,
      BLUR_TYPE_TO_BACKGROUND[blurType] ?? BLUR_TYPE_TO_BACKGROUND.regular
    ),
  };

  return (
    <View style={[styles.container, style]} {...rest}>
      {blurLayerStyles.map((layerStyle, index) => (
        <View
          key={index}
          style={[StyleSheet.absoluteFill, styles.layer, layerStyle]}
        />
      ))}
      <View style={[StyleSheet.absoluteFill, styles.layer, tintLayerStyle]} />
      {overlayColor != null && (
        <View
          style={[
            StyleSheet.absoluteFill,
            styles.layer,
            { backgroundColor: overlayColor },
          ]}
        />
      )}
      {children}
    </View>
  );
};

export default ProgressiveBlurView;

const styles = StyleSheet.create({
  container: {
    position: 'relative',
    overflow: 'hidden',
  },
  layer: {
    pointerEvents: 'none',
  },
});
