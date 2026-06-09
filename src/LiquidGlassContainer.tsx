import React from 'react';
import { Platform, View } from 'react-native';
import type { ViewProps } from 'react-native';
import ReactNativeLiquidGlassContainer from './ReactNativeLiquidGlassContainerNativeComponent';

export interface LiquidGlassContainerProps extends ViewProps {
  /**
   * Spacing between child glass surfaces (px).
   * @default 0
   * @platform iOS 26+
   */
  spacing?: number;
}

/**
 * A container that applies `UIGlassContainerEffect` (iOS 26+) to group
 * multiple `LiquidGlassView` children into a single unified glass surface.
 *
 * Falls back to a plain `View` on Android and iOS < 26.
 *
 * @example
 * ```tsx
 * <LiquidGlassContainer spacing={8}>
 *   <LiquidGlassView style={{ height: 60 }} />
 *   <LiquidGlassView style={{ height: 60 }} />
 * </LiquidGlassContainer>
 * ```
 */
export const LiquidGlassContainer: React.FC<LiquidGlassContainerProps> = ({
  spacing = 0,
  style,
  children,
  ...rest
}) => {
  const supportsGlassContainer =
    Platform.OS === 'ios' &&
    Number.parseInt(String(Platform.Version), 10) >= 26;

  if (!supportsGlassContainer) {
    return (
      <View style={style} {...rest}>
        {children}
      </View>
    );
  }

  return (
    <ReactNativeLiquidGlassContainer spacing={spacing} style={style} {...rest}>
      {children}
    </ReactNativeLiquidGlassContainer>
  );
};

export default LiquidGlassContainer;
